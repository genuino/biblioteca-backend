package br.com.biblioteca.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.biblioteca.domain.Configuracao;
import br.com.biblioteca.repository.ConfiguracaoRepository;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;

@Service
public class ConfiguracaoService {
	
	@Autowired
    private ConfiguracaoRepository configuracaoRepository;
	
	public Configuracao buscarConfiguracao() {
        
		List<Configuracao> confs = configuracaoRepository.findAll();
        
		return confs != null && !confs.isEmpty() ? confs.get(0) : new Configuracao();
    }

	public void salvar(Configuracao configuracao) throws Exception {

		System.out.println("==========3============");
		System.out.println("======================");

		Configuracao conf = configuracaoRepository.save(configuracao);
		
		if(conf == null) {
			
			throw new NotImplementedException("Dados não foram salvos. Tente novamente.");
		}
		
		configuracaoRepository.flush();
	}

}
