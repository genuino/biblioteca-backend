package br.com.biblioteca.controller;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.biblioteca.domain.LivroDTO;
import br.com.biblioteca.domain.LivroReserva;
import br.com.biblioteca.domain.Reserva;
import br.com.biblioteca.domain.ReservaDTO;
import br.com.biblioteca.service.CategoriaService;
import br.com.biblioteca.service.ConfiguracaoService;
import br.com.biblioteca.service.MultaService;
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
	MultaService multaService;

	@Autowired
	CategoriaService categoriaService;

	@Autowired
	VendaService vendaService;

	@Autowired
	ConfiguracaoService configuracaoService;

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	@PostMapping
	public ResponseEntity<String> salvarReserva(@RequestBody ReservaDTO reservaDTO) {
		
		String retornoErro = "";
		String retornoSucesso = "";
		
		//Verifica reservas do usuário
		/*List<Reserva> reservas = reservaService.pesquisarLivrosMatriculaReserva(List.of(reserva.getMatricula()),
				null);
		
		Configuracao configuracao = configuracaoService.buscarConfiguracao();
		
		if(configuracao.getQtasReservas() < reservas.size()) {
			
			retornoErro = "Usário com limites de reservas";
		}*/
		
		ReservaDTO reservaDTOBD = reservaDTO;
		if(reservaDTOBD.getLivros() != null) {
			
			reservaDTOBD.getLivros().clear();
		}
		
		for (LivroDTO livroDTO : reservaDTO.getLivros()) {
			
			String periodo = reservaService.livroReservado(livroDTO.getId(), reservaDTO.getIdAluno());
			
			if(periodo.isBlank()) {
				
				retornoErro = retornoErro.concat("Usuário já tem reserva para o livro"
						.concat(livroDTO.getTitulo()).concat(" na(s) data(s) de ").concat(periodo).concat(".\n"));
					
				continue;
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
			
			if(livrosDispon <= 0) {
				
				String retornoData = reservaDTO.getDataInicial();
				
				if(reservaDTO.getDataInicial().equals(reservaDTO.getDataFinal())) {
					
					retornoData = reservaDTO.getDataInicial().concat(" a ")
						.concat(reservaDTO.getDataFinal());
				}
				
				retornoErro = retornoErro.concat("Reserva indisponível para o livro"
					.concat(livroDTO.getTitulo()).concat(" na(s) data(s) de ").concat(retornoData).concat(".\n"));
				
				continue;
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
						
						continue;
					} else {
						
						quantasReservasInt = Integer.parseInt(Character.toString(quantasReservasArray[i]));

						if(quantasReservasInt > 0 && quantasReservasInt < reservaDTO.getCopia()) {
							
							retornoErro = retornoErro.concat("Disponível ".concat(Integer.toString(quantasReservasInt))
									.concat(" reserva(s) para o livro ").concat(livroDTO.getTitulo()).concat(".\n"));
							
							continue;
						}
					}
					
				} else {
					
					if(quantasReservasArray[i] == '-') {
						
						retornoErro = retornoErro.concat("Reserva esgotada para o mesmo livro: ".concat(livroDTO.getTitulo()).concat(".\n"));
					} else {
						
						quantasReservasMesmoLivro = Integer.parseInt(Character.toString(quantasReservasArray[i]));
						
						if(quantasReservasMesmoLivro > 0 && quantasReservasMesmoLivro < reservaDTO.getCopia()) {
							
							retornoErro = retornoErro.concat("Disponível ".concat(Integer.toString(quantasReservasMesmoLivro))
									.concat(" reserva(s) para o livro ").concat(livroDTO.getTitulo()).concat(".\n"));
						}
					}
					
				}
				
				if(retornoErro.isEmpty()) {
					
					reservaDTOBD.getLivros().add(livroDTO);
					
				}
				
			}
				
		}
		
		Reserva reservaRet = reservaService.salvarReserva(Util.toEntity(reservaDTOBD));
		
		if(reservaRet != null) {
			
			for(LivroDTO livroDTO : reservaDTOBD.getLivros()) {
				
				retornoSucesso = retornoSucesso.concat("Reserva para o livro ".concat(livroDTO.getTitulo()).concat(" feita com sucesso!\n"));
			}
		} else {
			
			for(LivroDTO livroDTO : reservaDTOBD.getLivros()) {
				
				retornoErro = retornoErro.concat("Reserva para o livro ".concat(livroDTO.getTitulo()).concat(" não realizada.\n"));
			}
		}
		
		
		return retornoErro.isBlank() ? ResponseEntity.ok(retornoSucesso) : 
			ResponseEntity.ofNullable(retornoSucesso.concat(retornoErro));
	}
	
	@GetMapping("/pesquisar_reserva/{paginas}")
    public ResponseEntity<List<ReservaDTO>> buscarTodasReservas(@PathVariable int paginas) throws Exception {

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
			}
			
			LivroDTO livroDTO = Util.formatarLivroParaLivroDTO(livroReserva.getLivro());
			reservaDTO.getLivros().add(livroDTO);
			
			dados.add(reservaDTO);
		}
		
		return dados;
	}
	
}
