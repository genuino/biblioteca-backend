package br.com.biblioteca.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.biblioteca.repository.PenalizacaoRepository;

@Service
public class PenalizacaoService {

	@Autowired
	PenalizacaoRepository penalizacaoRepository;
	
	public LocalDate clientePenalizado(Integer idAluno, int idCliente) {
		
		LocalDate dataFinal = penalizacaoRepository.findByDataFinal(idAluno, idCliente);
		
		if(dataFinal != null) {
			
			return dataFinal;
		} 
		
		return null;
	}
}
