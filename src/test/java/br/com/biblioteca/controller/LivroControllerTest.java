package br.com.biblioteca.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import br.com.biblioteca.domain.Autor;
import br.com.biblioteca.domain.AutorDTO;
import br.com.biblioteca.domain.Livro;
import br.com.biblioteca.domain.LivroDTO;
import br.com.biblioteca.service.AutorService;
import br.com.biblioteca.service.CategoriaService;
import br.com.biblioteca.service.LivroService;
import br.com.biblioteca.service.PagamentoService;
import br.com.biblioteca.service.PenalizacaoService;
import br.com.biblioteca.service.ReservaService;
import br.com.biblioteca.service.VendaService;
import br.com.biblioteca.util.Util;

/**
 * NOTE: os nomes de pacote/classe dos services e o pacote do controller
 * (br.com.biblioteca.controller) foram assumidos com base no padrão do
 * restante do projeto. Ajuste os imports se divergirem do seu projeto real.
 *
 * A classe Util é mockada de duas formas:
 *  - mockStatic(Util.class) para o método estático Util.toEntity(...)
 *  - mockConstruction(Util.class) para a chamada "new Util().criarQRCode(...)",
 *    evitando que o teste escreva arquivos/QRCode de verdade em disco.
 */
@ExtendWith(MockitoExtension.class)
class LivroControllerTest {

	@Mock
	private LivroService livroService;

	@Mock
	private CategoriaService categoriaService;

	@Mock
	private AutorService autorService;

	@Mock
	private ReservaService reservaService;

	@Mock
	private PenalizacaoService penalizacaoService;

	@Mock
	private PagamentoService multaService;

	@Mock
	private VendaService vendaService;

	@InjectMocks
	private LivroController livroController;

	private LivroDTO livroDTO;
	private AutorDTO autorDTO;

	@BeforeEach
	void setUp() {
		livroController = new LivroController();
		// Reflection-free injeção manual, já que @InjectMocks não cobre @Value.
		// Caso seu controller use construtor, prefira injetar via construtor aqui.
		setField("livroService", livroService);
		setField("categoriaService", categoriaService);
		setField("autorService", autorService);
		setField("reservaService", reservaService);
		setField("penalizacaoService", penalizacaoService);
		setField("multaService", multaService);
		setField("vendaService", vendaService);
		setField("uploadPath", "/tmp/uploads-teste");

		autorDTO = new AutorDTO();
		autorDTO.setNome("Machado de Assis");

		livroDTO = new LivroDTO();
		livroDTO.setId(null);
		livroDTO.setTitulo("Dom Casmurro");
		livroDTO.setAutores(new ArrayList<>(List.of(autorDTO)));
	}

	private void setField(String name, Object value) {
		try {
			var field = LivroController.class.getDeclaredField(name);
			field.setAccessible(true);
			field.set(livroController, value);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Campo '" + name + "' não encontrado em LivroController. "
					+ "Ajuste o nome do campo no teste conforme o controller real.", e);
		}
	}

	@Test
	void salvarLivro_deveRetornarOk_quandoCadastroComSucesso() {
		Livro livroMapeado = new Livro();
		livroMapeado.setTitulo("Dom Casmurro");

		Autor autorNovo = new Autor();
		autorNovo.setNome("Machado de Assis");

		Livro livroSalvo = new Livro();
		livroSalvo.setId(1);
		livroSalvo.setTitulo("Dom Casmurro");

		try (MockedStatic<Util> utilStatic = mockStatic(Util.class);
				MockedConstruction<Util> utilConstruction = mockConstruction(Util.class)) {

			utilStatic.when(() -> Util.toEntity(livroDTO)).thenReturn(livroMapeado);
			when(autorService.buscarNomeAutor("Machado de Assis")).thenReturn(null);
			utilStatic.when(() -> Util.autorToEntity(autorDTO)).thenReturn(autorNovo);
			when(autorService.criar(autorNovo)).thenReturn(autorNovo);
			when(livroService.salvarLivro(any(Livro.class))).thenReturn(livroSalvo);

			ResponseEntity<Object> response = livroController.salvarLivro(livroDTO, false);

			assertEquals(200, response.getStatusCode().value());
			@SuppressWarnings("unchecked")
			Map<String, Object> body = (Map<String, Object>) response.getBody();
			assertEquals(1, body.get("id"));
			assertEquals("Dom Casmurro", body.get("titulo"));
			assertEquals("Livro cadastrado com sucesso!", body.get("message"));
			assertTrue(((String) body.get("qrCodeUrl")).contains("livro_qrCode_1"));

			verify(livroService).salvarLivro(any(Livro.class));
			// garante que nenhum QR code real foi gerado em disco
			assertEquals(1, utilConstruction.constructed().size());
			verify(utilConstruction.constructed().get(0)).criarQRCode(anyString(), anyString(), anyString(), anyString());
		}
	}

	@Test
	void salvarLivro_deveReutilizarAutorExistente_quandoAutorJaCadastradoNoBanco() {
		Livro livroMapeado = new Livro();
		Autor autorExistente = new Autor();
		autorExistente.setId(5);
		autorExistente.setNome("Machado de Assis");

		Livro livroSalvo = new Livro();
		livroSalvo.setId(2);
		livroSalvo.setTitulo("Dom Casmurro");

		try (MockedStatic<Util> utilStatic = mockStatic(Util.class);
				MockedConstruction<Util> utilConstruction = mockConstruction(Util.class)) {

			utilStatic.when(() -> Util.toEntity(livroDTO)).thenReturn(livroMapeado);
			when(autorService.buscarNomeAutor("Machado de Assis")).thenReturn(autorExistente);
			when(livroService.salvarLivro(any(Livro.class))).thenReturn(livroSalvo);

			livroController.salvarLivro(livroDTO, false);

			// autor já existente: não deve criar um novo autor
			verify(autorService, never()).criar(any(Autor.class));

			Set<Autor> autoresEsperado = new HashSet<>();
			autoresEsperado.add(autorExistente);
			assertEquals(autoresEsperado, livroMapeado.getAutores());
		}
	}

	@Test
	void salvarLivro_deveRetornarMensagemDuplicidade_quandoComRestricaoEExisteLivroComMesmosAutores() {
		Livro livroMapeado = new Livro();
		livroMapeado.setTitulo("Dom Casmurro");

		Autor autorExistente = new Autor();
		autorExistente.setNome("Machado de Assis");

		try (MockedStatic<Util> utilStatic = mockStatic(Util.class);
				MockedConstruction<Util> utilConstruction = mockConstruction(Util.class)) {

			utilStatic.when(() -> Util.toEntity(livroDTO)).thenReturn(livroMapeado);
			when(autorService.buscarNomeAutor("Machado de Assis")).thenReturn(autorExistente);
			when(livroService.buscarLivroAutor(eq("Dom Casmurro"), any()))
					.thenReturn(new ArrayList<>(List.of("Machado de Assis")));

			ResponseEntity<Object> response = livroController.salvarLivro(livroDTO, true);

			@SuppressWarnings("unchecked")
			Map<String, Object> body = (Map<String, Object>) response.getBody();
			assertTrue(((String) body.get("message")).contains("mesmo título"));
			verify(livroService, never()).salvarLivro(any(Livro.class));
		}
	}

	@Test
	void salvarLivro_deveRetornarMensagemDeErro_quandoServiceRetornaNull() {
		Livro livroMapeado = new Livro();

		try (MockedStatic<Util> utilStatic = mockStatic(Util.class);
				MockedConstruction<Util> utilConstruction = mockConstruction(Util.class)) {

			utilStatic.when(() -> Util.toEntity(livroDTO)).thenReturn(livroMapeado);
			when(autorService.buscarNomeAutor("Machado de Assis")).thenReturn(new Autor());
			when(livroService.salvarLivro(any(Livro.class))).thenReturn(null);

			ResponseEntity<Object> response = livroController.salvarLivro(livroDTO, false);

			@SuppressWarnings("unchecked")
			Map<String, Object> body = (Map<String, Object>) response.getBody();
			assertEquals("Cadastro do livro não realizado.", body.get("message"));
			assertTrue(utilConstruction.constructed().isEmpty());
		}
	}
}
