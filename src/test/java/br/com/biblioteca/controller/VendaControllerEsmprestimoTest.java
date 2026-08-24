package br.com.biblioteca.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import br.com.biblioteca.domain.ClienteDTO;
import br.com.biblioteca.domain.Configuracao;
import br.com.biblioteca.domain.Livro;
import br.com.biblioteca.domain.LivroDTO;
import br.com.biblioteca.domain.Venda;
import br.com.biblioteca.domain.VendaDTO;
import br.com.biblioteca.service.ConfiguracaoService;
import br.com.biblioteca.service.PagamentoService;
import br.com.biblioteca.service.PenalizacaoService;
import br.com.biblioteca.service.ReservaService;
import br.com.biblioteca.service.VendaService;
import br.com.biblioteca.util.Util;

/**
 * NOTE: assumi que este método está em VendaController, com os campos
 * configuracaoService, vendaService, multaService (PagamentoService),
 * penalizacaoService e reservaService — ajuste os imports/nomes conforme
 * o controller real caso divirjam.
 *
 * Util.toEntity(...) e Util.formatLocalDatetoString(...) são estáticos,
 * então são mockados com mockStatic dentro de cada teste.
 */
@ExtendWith(MockitoExtension.class)
class VendaControllerEmprestimoTest {

	@Mock
	private ConfiguracaoService configuracaoService;

	@Mock
	private VendaService vendaService;

	@Mock
	private PagamentoService multaService;

	@Mock
	private PenalizacaoService penalizacaoService;

	@Mock
	private ReservaService reservaService;

	private VendaController vendaController;

	private VendaDTO vendaDTO;
	private LivroDTO livroDTO;
	private Configuracao configuracao;
	private static final Integer ID_ALUNO = 10;

	@BeforeEach
	void setUp() {
		vendaController = new VendaController();
		setField("configuracaoService", configuracaoService);
		setField("vendaService", vendaService);
		setField("multaService", multaService);
		setField("penalizacaoService", penalizacaoService);
		setField("reservaService", reservaService);

		livroDTO = new LivroDTO();
		livroDTO.setId(1);
		livroDTO.setTitulo("Dom Casmurro");

		ClienteDTO clienteDTO = new ClienteDTO();
		clienteDTO.setId(99);

		vendaDTO = new VendaDTO();
		vendaDTO.setIdAluno(ID_ALUNO);
		vendaDTO.setCliente(clienteDTO);
		vendaDTO.setLivros(Set.of(livroDTO));

		configuracao = new Configuracao();
		configuracao.setQtosDiasReserva(7);
	}

	private void setField(String name, Object value) {
		try {
			var field = VendaController.class.getDeclaredField(name);
			field.setAccessible(true);
			field.set(vendaController, value);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Campo '" + name + "' não encontrado em VendaController. "
					+ "Ajuste o nome do campo no teste conforme o controller real.", e);
		}
	}

	@Test
	void emprestimo_deveRetornarOk_quandoNenhumaRestricaoImpedeOEmprestimo() {
		Venda vendaMapeada = new Venda();
		Livro livroMapeado = new Livro();
		livroMapeado.setTitulo("Dom Casmurro");

		try (MockedStatic<Util> utilStatic = mockStatic(Util.class)) {
			utilStatic.when(() -> Util.toEntity(vendaDTO)).thenReturn(vendaMapeada);
			utilStatic.when(() -> Util.toEntity(livroDTO)).thenReturn(livroMapeado);
			utilStatic.when(() -> Util.formatLocalDatetoString(any(LocalDate.class))).thenCallRealMethod();

			when(configuracaoService.buscarConfiguracao()).thenReturn(configuracao);
			when(vendaService.buscarLivroEmprestado(1, ID_ALUNO)).thenReturn(1);
			when(vendaService.buscarMesmoLivroEmprestado(1, ID_ALUNO)).thenReturn(0);
			when(multaService.clienteMultado(ID_ALUNO, 99)).thenReturn(0);
			when(penalizacaoService.clientePenalizado(ID_ALUNO, 99)).thenReturn(null);
			when(reservaService.verificarReservaDatas(eq(1), any(LocalDate.class), any(LocalDate.class))).thenReturn(0);
			when(reservaService.livroReservado(1, ID_ALUNO)).thenReturn(null);

			ResponseEntity<String> response = vendaController.emprestimo(vendaDTO);

			assertEquals(200, response.getStatusCode().value());
			assertTrue(response.getBody().contains("Empréstimo realizado com sucesso para o livro Dom Casmurro"));

			@SuppressWarnings("unchecked")
			var livrosCaptor = org.mockito.ArgumentCaptor.forClass(Set.class);
			verify(vendaService).salvarVenda(livrosCaptor.capture(), eq(vendaMapeada));
			assertEquals(1, livrosCaptor.getValue().size());
		}
	}

	@Test
	void emprestimo_deveRetornarBadRequest_quandoLimiteDeEmprestimosExcedido() {
		Venda vendaMapeada = new Venda();

		try (MockedStatic<Util> utilStatic = mockStatic(Util.class)) {
			utilStatic.when(() -> Util.toEntity(vendaDTO)).thenReturn(vendaMapeada);

			when(configuracaoService.buscarConfiguracao()).thenReturn(configuracao);
			when(vendaService.buscarLivroEmprestado(1, ID_ALUNO)).thenReturn(0);
			when(vendaService.buscarMesmoLivroEmprestado(1, ID_ALUNO)).thenReturn(0);
			when(multaService.clienteMultado(ID_ALUNO, 99)).thenReturn(0);
			when(penalizacaoService.clientePenalizado(ID_ALUNO, 99)).thenReturn(null);
			when(reservaService.verificarReservaDatas(eq(1), any(LocalDate.class), any(LocalDate.class))).thenReturn(1);

			ResponseEntity<String> response = vendaController.emprestimo(vendaDTO);

			assertEquals(400, response.getStatusCode().value());
			assertTrue(response.getBody().contains("Limite de empréstimos de livros excedidos."));
			verify(vendaService, never()).salvarVenda(any(), any());
		}
	}

	@Test
	void emprestimo_deveRetornarBadRequest_quandoClienteEstaMultado() {
		Venda vendaMapeada = new Venda();

		try (MockedStatic<Util> utilStatic = mockStatic(Util.class)) {
			utilStatic.when(() -> Util.toEntity(vendaDTO)).thenReturn(vendaMapeada);

			when(configuracaoService.buscarConfiguracao()).thenReturn(configuracao);
			when(vendaService.buscarLivroEmprestado(1, ID_ALUNO)).thenReturn(1);
			when(vendaService.buscarMesmoLivroEmprestado(1, ID_ALUNO)).thenReturn(0);
			when(multaService.clienteMultado(ID_ALUNO, 99)).thenReturn(-2);
			when(penalizacaoService.clientePenalizado(ID_ALUNO, 99)).thenReturn(null);
			when(reservaService.verificarReservaDatas(eq(1), any(LocalDate.class), any(LocalDate.class))).thenReturn(1);

			ResponseEntity<String> response = vendaController.emprestimo(vendaDTO);

			assertEquals(400, response.getStatusCode().value());
			assertTrue(response.getBody().contains("multa(s)"));
			verify(vendaService, never()).salvarVenda(any(), any());
		}
	}

	@Test
	void emprestimo_deveRetornarBadRequest_quandoLivroEstaReservadoParaOutraData() {
		Venda vendaMapeada = new Venda();

		try (MockedStatic<Util> utilStatic = mockStatic(Util.class)) {
			utilStatic.when(() -> Util.toEntity(vendaDTO)).thenReturn(vendaMapeada);
			utilStatic.when(() -> Util.formatLocalDatetoString(any(LocalDate.class))).thenCallRealMethod();

			when(configuracaoService.buscarConfiguracao()).thenReturn(configuracao);
			when(vendaService.buscarLivroEmprestado(1, ID_ALUNO)).thenReturn(1);
			when(vendaService.buscarMesmoLivroEmprestado(1, ID_ALUNO)).thenReturn(0);
			when(multaService.clienteMultado(ID_ALUNO, 99)).thenReturn(0);
			when(penalizacaoService.clientePenalizado(ID_ALUNO, 99)).thenReturn(null);
			when(reservaService.verificarReservaDatas(eq(1), any(LocalDate.class), any(LocalDate.class))).thenReturn(0);
			when(reservaService.livroReservado(1, ID_ALUNO)).thenReturn("01/01/2030,");

			ResponseEntity<String> response = vendaController.emprestimo(vendaDTO);

			assertEquals(400, response.getStatusCode().value());
			assertTrue(response.getBody().contains("reservado nas datas"));
			verify(vendaService, never()).salvarVenda(any(), any());
		}
	}

	@Test
	void emprestimo_deveRetornarBadRequestComBodyNulo_quandoOcorreExcecao() {
		when(configuracaoService.buscarConfiguracao()).thenThrow(new RuntimeException("Falha ao buscar configuração"));

		ResponseEntity<String> response = vendaController.emprestimo(vendaDTO);

		assertEquals(400, response.getStatusCode().value());
		assertNull(response.getBody());
	}
}