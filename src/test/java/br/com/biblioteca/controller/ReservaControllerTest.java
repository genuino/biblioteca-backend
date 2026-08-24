package br.com.biblioteca.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.com.biblioteca.domain.ClienteDTO;
import br.com.biblioteca.domain.Configuracao;
import br.com.biblioteca.domain.LivroDTO;
import br.com.biblioteca.domain.LivroReserva;
import br.com.biblioteca.domain.Reserva;
import br.com.biblioteca.domain.ReservaDTO;
import br.com.biblioteca.util.Util;
import br.com.biblioteca.service.ConfiguracaoService;
import br.com.biblioteca.service.ReservaService;


@ExtendWith(MockitoExtension.class)
class ReservaControllerTest {

	@Mock
	private ReservaService reservaService;

	@Mock
	private ConfiguracaoService configuracaoService;

	@InjectMocks
	private ReservaController reservaController;

	private ReservaDTO reservaDTO;
	private LivroDTO livroDTO;
	private Configuracao configuracao;

	@BeforeEach
	void setUp() {
		livroDTO = new LivroDTO();
		livroDTO.setId(1);
		livroDTO.setTitulo("Dom Casmurro");

		reservaDTO = new ReservaDTO();
		reservaDTO.setId(0);
		reservaDTO.setIdAluno(10);
		reservaDTO.setCopia(1);
		reservaDTO.setDataInicial("2026-09-01");
		reservaDTO.setDataFinal("2026-09-05");
		reservaDTO.setClienteDTO(new ClienteDTO());

		List<LivroDTO> livros = new ArrayList<>();
		livros.add(livroDTO);
		reservaDTO.setLivros(livros);

		configuracao = new Configuracao();
		configuracao.setQtasReservas(5);
	}

	@Test
	void deveSalvarReservaComSucessoQuandoNaoHaRestricoes() {
		when(reservaService.pesquisarLivrosMatriculaReserva(eq(List.of(10)), eq(null)))
				.thenReturn(Collections.emptyList());
		when(configuracaoService.buscarConfiguracao()).thenReturn(configuracao);
		when(reservaService.livroReservado(1, 10)).thenReturn("");
		when(reservaService.verificarReservaDatas(eq(1), any(LocalDate.class), any(LocalDate.class)))
				.thenReturn(0);
		// "55": nenhum '-', dígitos > copia solicitada (1) -> não gera restrição
		when(reservaService.restricoesReservarLivro(1, 10, -1)).thenReturn("55");

		Reserva reservaSalva = new Reserva();
		when(reservaService.salvarReserva(anyList())).thenReturn(reservaSalva);

		try (MockedStatic<Util> utilMock = mockStatic(Util.class)) {
			utilMock.when(() -> Util.toEntity(reservaDTO)).thenReturn(new Reserva());
			utilMock.when(() -> Util.toEntity(livroDTO)).thenReturn(new br.com.biblioteca.domain.Livro());
			utilMock.when(() -> Util.formatStringtoLocalDate("2026-09-01")).thenReturn(LocalDate.of(2026, 9, 1));
			utilMock.when(() -> Util.formatStringtoLocalDate("2026-09-05")).thenReturn(LocalDate.of(2026, 9, 5));

			ResponseEntity<String> response = reservaController.salvarReserva(reservaDTO);

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(response.getBody()).contains("Reserva para o livro Dom Casmurro feita com sucesso!");
		}

		verify(reservaService).salvarReserva(anyList());
	}

	@Test
	void deveRetornarErroQuandoUsuarioAtingiuLimiteDeReservas() {
		// 2 reservas já existentes > limite configurado de 1
		when(reservaService.pesquisarLivrosMatriculaReserva(eq(List.of(10)), eq(null)))
				.thenReturn(List.of(new LivroReserva(), new LivroReserva()));

		Configuracao configLimitada = new Configuracao();
		configLimitada.setQtasReservas(1);
		when(configuracaoService.buscarConfiguracao()).thenReturn(configLimitada);

		try (MockedStatic<Util> utilMock = mockStatic(Util.class)) {
			utilMock.when(() -> Util.toEntity(reservaDTO)).thenReturn(new Reserva());

			ResponseEntity<String> response = reservaController.salvarReserva(reservaDTO);

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			assertThat(response.getBody()).contains("Usário com limites de reservas");
		}

		// lista de livros foi limpa -> nunca deveria tentar salvar
		verify(reservaService, never()).salvarReserva(anyList());
	}

	@Test
	void deveRetornarErroQuandoLivroJaEstaReservadoPeloUsuario() {
		when(reservaService.pesquisarLivrosMatriculaReserva(eq(List.of(10)), eq(null)))
				.thenReturn(Collections.emptyList());
		when(configuracaoService.buscarConfiguracao()).thenReturn(configuracao);
		when(reservaService.livroReservado(1, 10)).thenReturn("01/09/2026 a 05/09/2026");

		try (MockedStatic<Util> utilMock = mockStatic(Util.class)) {
			utilMock.when(() -> Util.toEntity(reservaDTO)).thenReturn(new Reserva());
			utilMock.when(() -> Util.formatStringtoLocalDate("2026-09-01")).thenReturn(LocalDate.of(2026, 9, 1));
			utilMock.when(() -> Util.formatStringtoLocalDate("2026-09-05")).thenReturn(LocalDate.of(2026, 9, 5));
			when(reservaService.verificarReservaDatas(eq(1), any(LocalDate.class), any(LocalDate.class)))
					.thenReturn(0);
			when(reservaService.restricoesReservarLivro(1, 10, -1)).thenReturn("55");

			ResponseEntity<String> response = reservaController.salvarReserva(reservaDTO);

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			assertThat(response.getBody()).contains("Usuário já tem reserva para o livro Dom Casmurro");
		}
	}

	@Test
	void deveRetornarErroQuandoNaoHaExemplaresDisponiveisNoPeriodo() {
		when(reservaService.pesquisarLivrosMatriculaReserva(eq(List.of(10)), eq(null)))
				.thenReturn(Collections.emptyList());
		when(configuracaoService.buscarConfiguracao()).thenReturn(configuracao);
		when(reservaService.livroReservado(1, 10)).thenReturn("");

		try (MockedStatic<Util> utilMock = mockStatic(Util.class)) {
			utilMock.when(() -> Util.toEntity(reservaDTO)).thenReturn(new Reserva());
			utilMock.when(() -> Util.formatStringtoLocalDate("2026-09-01")).thenReturn(LocalDate.of(2026, 9, 1));
			utilMock.when(() -> Util.formatStringtoLocalDate("2026-09-05")).thenReturn(LocalDate.of(2026, 9, 5));
			when(reservaService.verificarReservaDatas(eq(1), any(LocalDate.class), any(LocalDate.class)))
					.thenReturn(1); // > 0 => indisponível
			when(reservaService.restricoesReservarLivro(1, 10, -1)).thenReturn("55");

			ResponseEntity<String> response = reservaController.salvarReserva(reservaDTO);

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			assertThat(response.getBody()).contains("Reserva indisponível para o livro");
		}
	}

	@Test
	void deveRetornarErroQuandoReservasEstaoEsgotadasParaOLivro() {
		when(reservaService.pesquisarLivrosMatriculaReserva(eq(List.of(10)), eq(null)))
				.thenReturn(Collections.emptyList());
		when(configuracaoService.buscarConfiguracao()).thenReturn(configuracao);
		when(reservaService.livroReservado(1, 10)).thenReturn("");

		try (MockedStatic<Util> utilMock = mockStatic(Util.class)) {
			utilMock.when(() -> Util.toEntity(reservaDTO)).thenReturn(new Reserva());
			utilMock.when(() -> Util.formatStringtoLocalDate("2026-09-01")).thenReturn(LocalDate.of(2026, 9, 1));
			utilMock.when(() -> Util.formatStringtoLocalDate("2026-09-05")).thenReturn(LocalDate.of(2026, 9, 5));
			when(reservaService.verificarReservaDatas(eq(1), any(LocalDate.class), any(LocalDate.class)))
					.thenReturn(0);
			// primeiro caractere '-' => esgotada
			when(reservaService.restricoesReservarLivro(1, 10, -1)).thenReturn("-5");

			ResponseEntity<String> response = reservaController.salvarReserva(reservaDTO);

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			assertThat(response.getBody()).contains("Reserva esgotada para o livro Dom Casmurro");
		}

		verify(reservaService, never()).salvarReserva(anyList());
	}

	@Test
	void deveRetornarBadRequestQuandoServiceFalharAoSalvar() {
		when(reservaService.pesquisarLivrosMatriculaReserva(eq(List.of(10)), eq(null)))
				.thenReturn(Collections.emptyList());
		when(configuracaoService.buscarConfiguracao()).thenReturn(configuracao);
		when(reservaService.livroReservado(1, 10)).thenReturn("");
		when(reservaService.verificarReservaDatas(eq(1), any(LocalDate.class), any(LocalDate.class)))
				.thenReturn(0);
		when(reservaService.restricoesReservarLivro(1, 10, -1)).thenReturn("55");
		when(reservaService.salvarReserva(anyList())).thenReturn(null);

		try (MockedStatic<Util> utilMock = mockStatic(Util.class)) {
			utilMock.when(() -> Util.toEntity(reservaDTO)).thenReturn(new Reserva());
			utilMock.when(() -> Util.toEntity(livroDTO)).thenReturn(new br.com.biblioteca.domain.Livro());
			utilMock.when(() -> Util.formatStringtoLocalDate("2026-09-01")).thenReturn(LocalDate.of(2026, 9, 1));
			utilMock.when(() -> Util.formatStringtoLocalDate("2026-09-05")).thenReturn(LocalDate.of(2026, 9, 5));

			ResponseEntity<String> response = reservaController.salvarReserva(reservaDTO);

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			assertThat(response.getBody()).contains("Não foi possível cadastrar a reserva");
		}
	}
}