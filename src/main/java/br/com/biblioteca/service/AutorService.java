package br.com.biblioteca.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.biblioteca.domain.Autor;
import br.com.biblioteca.repository.AutorRepository;

@Service
public class AutorService {

	@Autowired
    private AutorRepository autorRepository;
	
	public List<Autor> buscarPorNome(String nome) {
        
		List<Autor> autores = autorRepository.findByNomeContainingIgnoreCase(nome);
        return autores == null ? new ArrayList<>() : autores;
    }

	public Autor buscarNomeAutor(String nome) {
        
		Autor autor = autorRepository.findByNomeIgnoreCase(nome);
        
		return autor;
    }
    
    public Autor criar(Autor autor) {
        
    	Autor novoAutor = autorRepository.save(autor);
        
    	autorRepository.flush();
    	
    	return novoAutor == null ? new Autor() : novoAutor;
    }
    
    public void deletarTudo() {
    	
    	autorRepository.deleteAll();
    }
}
