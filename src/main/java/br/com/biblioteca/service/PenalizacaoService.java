package br.com.biblioteca.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.biblioteca.domain.Pagamento;
import br.com.biblioteca.domain.Penalizacao;
import br.com.biblioteca.repository.PagamentoRepository;
import br.com.biblioteca.repository.PenalizacaoRepository;
import jakarta.transaction.Transactional;

@Service
public class PenalizacaoService {

	@Autowired
	PenalizacaoRepository penalizacaoRepository;
	
	@Autowired
	PagamentoRepository pagamentoRepository;
	
	public LocalDate clientePenalizado(Integer idAluno, int idCliente) {
		
		LocalDate dataFinal = penalizacaoRepository.findByDataFinal(idAluno, idCliente);
		
		if(dataFinal != null) {
			
			return dataFinal;
		} 
		
		return null;
	}
	
	public List<Penalizacao> buscarPenalizacoes(Integer idEscola, Integer deslocamento) {
		
		Pageable pageable = PageRequest.of(deslocamento -1000, deslocamento);
		
		return penalizacaoRepository.buscarPenalizacoes(idEscola, pageable);
	}

	public List<Penalizacao> buscarPenalizacoesCampos(Integer idEscola, Integer idAluno, List<Integer> idLivros) {
		
		return penalizacaoRepository.buscarPenalizacoesCampos(idEscola, idAluno, idLivros);
	}
	
	@Transactional
	public void salvar(Penalizacao penalizacao, Pagamento pagamento) {
     
		if (penalizacao != null) {
			
			penalizacaoRepository.save(penalizacao);
        }
        if (pagamento != null) {
        	
            pagamentoRepository.save(pagamento);
        }
	}
}
