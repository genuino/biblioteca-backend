package br.com.biblioteca.controller;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.biblioteca.domain.Configuracao;
import br.com.biblioteca.domain.LivroDTO;
import br.com.biblioteca.domain.LivroReserva;
import br.com.biblioteca.domain.Reserva;
import br.com.biblioteca.domain.ReservaDTO;
import br.com.biblioteca.service.CategoriaService;
import br.com.biblioteca.service.ConfiguracaoService;
import br.com.biblioteca.service.PagamentoService;
import br.com.biblioteca.service.PenalizacaoService;
import br.com.biblioteca.service.ReservaService;
import br.com.biblioteca.service.VendaService;
import br.com.biblioteca.util.Util;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/biblioteca/reserva")
public class ReservaController {

	@Autowired
	ReservaService reservaService;

	@Autowired
	PenalizacaoService penalizacaoService;

	@Autowired
	PagamentoService multaService;

	@Autowired
	CategoriaService categoriaService;

	@Autowired
	VendaService vendaService;

	@Autowired
	ConfiguracaoService configuracaoService;

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private static final Logger logger = LogManager.getLogger(AutorController.class);
	
	@PostMapping
	public ResponseEntity<String> salvarReserva(@RequestBody ReservaDTO reservaDTO) {
		
		String retornoErro = "";
		String retornoSucesso = "";
		
		//Verifica reservas do usuário
		List<LivroReserva> reservas = reservaService.pesquisarLivrosMatriculaReserva(List.of(reservaDTO.getIdAluno()),
				null);
		
		Configuracao configuracao = configuracaoService.buscarConfiguracao();
		
		if(configuracao.getQtasReservas() <= reservas.size()) {
			
			retornoErro = "Usário com limites de reservas";
			
			return ResponseEntity.badRequest().body(retornoErro);
		}
		
		Reserva reservaRet = Util.toEntity(reservaDTO);
		List<LivroReserva> livrosReserva = new ArrayList<>();
		for (LivroDTO livroDTO : reservaDTO.getLivros()) {
			
			boolean adicionarLivrosReserva = true;
			
			String periodo = reservaService.livroReservado(livroDTO.getId(), reservaDTO.getIdAluno());
			
			if(!periodo.isBlank()) {
				
				retornoErro = retornoErro.concat("Usuário já tem reserva para o livro "
						.concat(livroDTO.getTitulo()).concat(" na(s) data(s) de ").concat(periodo).concat(".\n"));
				
				adicionarLivrosReserva = false;
			}
			
			System.out.println("======================================");
			System.out.println("reserva.getDataInicial().format(formatter): "
					+ reservaDTO.getDataInicial());
			System.out.println("reserva.getDataFinal().format(formatter): "
					+ reservaDTO.getDataFinal());
			System.out.println("======================================");
			
			//Verifica se tem quantidade de livros suficiente para reserva no período
			int livrosDispon = reservaService.verificarReservaDatas(livroDTO.getId(), 
					Util.formatStringtoLocalDate(reservaDTO.getDataInicial()), 
					Util.formatStringtoLocalDate(reservaDTO.getDataFinal()));
			
			if(livrosDispon > 0) {
				
				String retornoData = reservaDTO.getDataInicial();
				
				if(reservaDTO.getDataInicial().equals(reservaDTO.getDataFinal())) {
					
					retornoData = reservaDTO.getDataInicial().concat(" a ")
						.concat(reservaDTO.getDataFinal());
				}
				
				retornoErro = retornoErro.concat("Reserva indisponível para o livro"
					.concat(livroDTO.getTitulo()).concat(" na(s) data(s) de ").concat(retornoData).concat(".\n"));
				
				adicionarLivrosReserva = false;
			} 
			
			//Verifica se o cliente não ultrapassou os limites de reservas
			String quantasReservas = reservaService.restricoesReservarLivro(livroDTO.getId(), reservaDTO.getIdAluno(),
					reservaDTO.getClienteDTO() != null ? reservaDTO.getClienteDTO().getId() : -1);
			
			char[] quantasReservasArray = quantasReservas.toCharArray();
			int quantasReservasInt = 0;
			int quantasReservasMesmoLivro = 0;
			
			for(int i = 0; i < quantasReservasArray.length;i++) {
				
				if(i == 0 && (quantasReservasArray[i] == '-' || quantasReservasArray[i] == '0')) {
					if(quantasReservasArray[i] == '-') {
						
						retornoErro = retornoErro.concat("Reserva esgotada para o livro ".concat(livroDTO.getTitulo()).
							concat(". Cancele uma ou mais para realizar novas reservas.").concat("\n"));
						
						adicionarLivrosReserva = false;
						
					} else {
						
						quantasReservasInt = Integer.parseInt(Character.toString(quantasReservasArray[i]));

						if(quantasReservasInt > 0 && quantasReservasInt < reservaDTO.getCopia()) {
							
							retornoErro = retornoErro.concat("Disponível ".concat(Integer.toString(quantasReservasInt))
									.concat(" reserva(s) para o livro ").concat(livroDTO.getTitulo()).concat(".\n"));
							
							adicionarLivrosReserva = false;
						}
					}
					
				} else {
					
					if(quantasReservasArray[i] == '-') {
						
						retornoErro = retornoErro.concat("Reserva esgotada para o mesmo livro: ".concat(livroDTO.getTitulo()).concat(".\n"));
						adicionarLivrosReserva = false;
					} else {
						
						quantasReservasMesmoLivro = Integer.parseInt(Character.toString(quantasReservasArray[i]));
						
						if(quantasReservasMesmoLivro > 0 && quantasReservasMesmoLivro < reservaDTO.getCopia()) {
							
							retornoErro = retornoErro.concat("Disponível ".concat(Integer.toString(quantasReservasMesmoLivro))
									.concat(" reserva(s) para o livro ").concat(livroDTO.getTitulo()).concat(".\n"));
							adicionarLivrosReserva = false;
						}
					}
					
				}
				
				if(adicionarLivrosReserva) {
					
					LivroReserva livroReserva = new LivroReserva();
					livroReserva.setLivro(Util.toEntity(livroDTO));
					livroReserva.setReserva(reservaRet);
					livroReserva.setDataFinal(
							reservaDTO.getDataFinal() != null
		                    ? Util.formatStringtoLocalDate(reservaDTO.getDataFinal())
		                    : null);
					livrosReserva.add(livroReserva);
					
					retornoSucesso = retornoSucesso.concat("Reserva para o livro ".concat(livroDTO.getTitulo()).concat(" feita com sucesso!\n"));
				}
				
			}
				
		}
		
		if(!livrosReserva.isEmpty()) {
			
			reservaRet = reservaService.salvarReserva(livrosReserva);
			
			if(reservaRet == null) {
				
				return ResponseEntity.badRequest().body("Não foi possível cadastrar a reserva. Tente novamente.");
			
			}
		}
		
		return retornoErro.isBlank() ? ResponseEntity.ok(retornoSucesso) : 
			ResponseEntity.badRequest().body(retornoSucesso.concat(retornoErro));
	}
	
	@GetMapping("/pesquisar_reserva/{paginas}")
    public ResponseEntity<List<ReservaDTO>> buscarTodasReservas(@PathVariable int paginas) throws Exception {
		
		logger.info("Entrou no buscarTodasReservas");
		System.out.println("===========================================");
		System.out.println("Entrou no buscarTodasReservas");
		System.out.println("===========================================");
		
		List<ReservaDTO> dados = new ArrayList<>();
		
		Pageable pageable = PageRequest.of(paginas - 1000, paginas);
		List<LivroReserva> livroReservas = reservaService.pesquisarTodasReserva(pageable);
		
		dados = formatarReservaParaReservaDTO(livroReservas);
		
		// Return ResponseEntity with image content type
        return ResponseEntity.ok()
                .body(dados);
    }
	

	@GetMapping("/buscar_reservas_nomes")
    public ResponseEntity<List<ReservaDTO>> get(@RequestParam(name = "nomes") List<Integer> nomes,
    		@RequestParam(name = "ids") List<Integer> ids) throws Exception {

		List<LivroReserva> livroReservas = reservaService.pesquisarLivrosMatriculaReserva(nomes, ids);
		
		List<ReservaDTO> dados = formatarReservaParaReservaDTO(livroReservas);
		
		// Return ResponseEntity with image content type
        return ResponseEntity.ok()
                .body(dados);
    }
	
	@GetMapping("/pesquisar_reserva_nomes_datas")
	public ResponseEntity<List<ReservaDTO>>  pesquisarReservaNomesDatas(
			@RequestParam(name = "idsLivros") List<Integer> idsLivros,
			@RequestParam(name = "matricula") String matricula,
			@RequestParam(name = "dataInicial") String dataInicial, 
			@RequestParam(name = "dataFinal") String dataFinal) {
		
		List<LivroReserva> livroReservas = reservaService.pesquisarReservaNomesDatas(idsLivros, 
			matricula, dataInicial, dataFinal);
		
		List<ReservaDTO> dados = formatarReservaParaReservaDTO(livroReservas);
		
		// Return ResponseEntity with image content type
        return ResponseEntity.ok()
                .body(dados);
	}
	
	private List<ReservaDTO> formatarReservaParaReservaDTO(List<LivroReserva> livroReservas) {
		
		List<ReservaDTO> dados = new ArrayList<>();
		Map<Integer, ReservaDTO> guardarReservasDTO = new HashMap<>();
		
		for (LivroReserva livroReserva : livroReservas) {
			
			ReservaDTO reservaDTO = guardarReservasDTO.get(livroReserva.getReserva().getId());
			if(reservaDTO == null) {
				
				reservaDTO = Util.toDTO(livroReserva.getReserva());
				reservaDTO.setId(livroReserva.getId());
			}
			
			reservaDTO.setDataFinal(Util.formatLocalDatetoString(livroReserva.getDataFinal()));
			
			Util util = new Util(categoriaService);
			LivroDTO livroDTO = util.formatarLivroParaLivroDTO(livroReserva.getLivro(), true);
			reservaDTO.getLivros().add(livroDTO);
			
			dados.add(reservaDTO);
		}
		
		return dados;
	}
	
	@PatchMapping("renovar/{id}")
	public ResponseEntity<String> atualizarParcial(
	        @PathVariable Integer id) {
		
		try {
		
			reservaService.renovarReserva(id);
			return ResponseEntity.ok("Reserva renovada com sucesso!");
		} catch(Exception ex) {
			
			return ResponseEntity.badRequest().body("Reserva não renovada. ".concat(ex.getMessage()));
		}
	}
	
	@DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable Integer id) {
        
		System.out.println("============ENTROOOOOOOOOOOOOOUuuuuuuuuuuuuuuuuuuuuu======");
		try {
		
			reservaService.deletarReserva(id);
			return ResponseEntity.ok()
	                .body("Exclusão realizada com sucesso!");
		} catch(Exception ex) {
			
			ex.printStackTrace();
			return ResponseEntity.badRequest()
	                .body("Exclusão não realizada. ".concat(ex.getMessage()));
		}
    }
	
}
