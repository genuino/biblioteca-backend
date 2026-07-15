package br.com.biblioteca.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.biblioteca.domain.LivroReserva;
import br.com.biblioteca.domain.Reserva;
import br.com.biblioteca.repository.ConfiguracaoRepository;
import br.com.biblioteca.repository.LivroReservaRepository;
import br.com.biblioteca.repository.ReservaRepository;

@Service
public class ReservaService {

	@Autowired
	ReservaRepository reservaRepository;

	@Autowired
	ConfiguracaoRepository configuracaoRepository;
	
	@Autowired
	LivroReservaRepository livroReservaRepository;
	
	public String livroReservado(int codLivro, Integer idAluno) {
		
		List<LocalDate[]> datasRervas = reservaRepository.findByCopia(codLivro, idAluno);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String datas = " ";
		
		if(datasRervas != null && !datasRervas.isEmpty()) {
			
			for(LocalDate[] datasLD : datasRervas) {
				
				if(!datasLD[0].format(formatter).trim().equals(
						datasLD[1].format(formatter).trim())) {
					
					datas = datas.concat(datasLD[0].format(formatter)).
							concat(" a ").concat(datasLD[1].format(formatter)).concat(",");
				} else {
					
					datas = datas.concat(datasLD[0].format(formatter)).concat(",");
				}
			}
			
		} 
		
		return datas.substring(0, datas.length() - 1);
	}
	

	public String restricoesReservarLivro(int idLivro, Integer idAluno, int idCliente) {
		
		int qtasReservas = Optional.ofNullable(
				reservaRepository.findByCopiaQuantosReservas(idAluno, idCliente)).orElse(0);
			
		
		String retornarQuantReservaRest = "0";		
		
		if(qtasReservas < 0) {
				
			retornarQuantReservaRest = Integer.toString(qtasReservas); 
		} 
				
		return retornarQuantReservaRest.trim();
		
	}
	
	public int verificarReservaDatas(int codLivro, LocalDate dataInicial, LocalDate dataFinal) {
		
		Integer reservasDisponiveis = reservaRepository.findByCopiaQuantosReservasLivroData(codLivro, 
			dataInicial, dataFinal);
		
		return reservasDisponiveis == null ? 0: reservasDisponiveis;
	}
	
	public Reserva salvarReserva(Reserva reserva) {
		
		Reserva reservaRet = reservaRepository.save(reserva);
		
		return reservaRet;
	}
	
	public List<LivroReserva> pesquisarLivrosMatriculaReserva(List<Integer> nomes, List<Integer> ids) {
		
		if(nomes == null || nomes.isEmpty()) {
			
			nomes = List.of(-1);
		}
		
		if(ids == null || ids.isEmpty() ) {
			
			ids = List.of(-1);
		}
		
		List<LivroReserva> reservaRet = livroReservaRepository.buscarReservasPorNomes(nomes, ids);
		
		return reservaRet;
	}

	public List<LivroReserva> pesquisarTodasReserva(Pageable page) {
		
		List<LivroReserva> livroReservaRet = livroReservaRepository.buscarTodasReservas(page);
		
		return livroReservaRet;
	}
	
	public List<LivroReserva> pesquisarReservaNomesDatas(List<Integer> idsLivros, String matricula, 
			String dataInicial, String dataFinal) {
		
		if (idsLivros == null || idsLivros.isEmpty()) {
			idsLivros = List.of(-1); // valor que nunca vai bater
		}
		
		
		
		List<LivroReserva> livroReservaRet = livroReservaRepository.buscarReservasPorNomesDatas(idsLivros, matricula,
				dataInicial, dataFinal);
		
		return livroReservaRet;
	}
}
