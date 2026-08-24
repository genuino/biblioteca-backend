package br.com.biblioteca.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.biblioteca.domain.Pagamento;
import br.com.biblioteca.repository.PagamentoRepository;

@Service
public class PagamentoService {
	
	@Autowired
	PagamentoRepository multaRepository;
	
	public int clienteMultado(Integer idAluno, int idCliente) {
		
		int qtasMultas = multaRepository.findByDataPagamento(idAluno, idCliente);
		
		if(qtasMultas < 0) {
			
			return qtasMultas;
		} 
		
		return 0;
	}

	public List<Pagamento> buscarPagamentos(Integer idEscola, Integer deslocamento) {
		
		Pageable pageable = PageRequest.of(deslocamento -1000, deslocamento);
		
		return multaRepository.buscarPagamentos(idEscola, pageable);
	}

	public List<Pagamento> buscarpagamentosCampos(Integer idEscola, Integer idAluno, List<Integer> idLivros) {
		
		return multaRepository.buscarPagamentosCampos(idEscola, idAluno, idLivros);
	}
	
	public Pagamento salvar(Pagamento pagamento) {
		
		pagamento = multaRepository.save(pagamento);
		
		return pagamento;
	}
}
