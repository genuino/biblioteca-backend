package br.com.biblioteca.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.biblioteca.repository.PagamentoRepository;

@Service
public class MultaService {
	
	@Autowired
	PagamentoRepository multaRepository;
	
	public int clienteMultado(Integer idAluno, int idCliente) {
		
		int qtasMultas = multaRepository.findByDataPagamento(idAluno, idCliente);
		
		if(qtasMultas < 0) {
			
			return qtasMultas;
		} 
		
		return 0;
	}
}
