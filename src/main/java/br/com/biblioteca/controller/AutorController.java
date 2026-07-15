package br.com.biblioteca.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.biblioteca.domain.Autor;
import br.com.biblioteca.service.AutorService;

@RestController
@RequestMapping("/biblioteca/autores")
public class AutorController {
    
	private static final Logger logger = LogManager.getLogger(AutorController.class);
	
    @Autowired
    private AutorService autorService;
    
    @GetMapping("/buscar")
    public ResponseEntity<List<Autor>> buscarPorNome(@RequestParam String nome) {
        
    	logger.info("No método buscarPorNome: '{}'", nome);
    	
    	List<Autor> autores = autorService.buscarPorNome(nome);
        return ResponseEntity.ok(autores);
    }
    
    @PostMapping
    public ResponseEntity<Autor> criar(@RequestBody Autor autor) {
        
    	logger.info("No método criar autor: '{}'", autor.getNome());
    	
    	/*if(true) {
    		
    		autorService.deletarTudo();
    		Set<Livro> livros = new HashSet<>();
    		return ResponseEntity.ok(new Autor(-1,"Autor", "", "", livros));
    	}*/
    	
    	List<Autor> autores = autorService.buscarPorNome(autor.getNome());
    	
    	autores = autores.stream()
    		.filter(n -> n.getNome().equals(autor.getNome())) 
    		.toList();
    	
    	if(autores != null && !autores.isEmpty()) {
    		
    		return ResponseEntity.ofNullable(new Autor(-1,"Nome do autor já cadastrado.", "", "", null));
    	}
    	
    	Autor novoAutor = autorService.criar(autor);
        return ResponseEntity.ok(novoAutor);
    }
}