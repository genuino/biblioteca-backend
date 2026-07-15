package br.com.biblioteca.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.biblioteca.domain.Categoria;
import br.com.biblioteca.domain.CategoriaDTO;
import br.com.biblioteca.service.CategoriaService;

@RestController
@RequestMapping("/biblioteca/categorias")
public class CategoriaController {

	private static final Logger logger = LogManager.getLogger(CategoriaController.class);
	
	@Autowired
    private CategoriaService categoriaService;
		
    // GET /api/categorias - Listar todas
    @GetMapping
    public List<CategoriaDTO> listar() {
    	
        List<Categoria> categorias = categoriaService.buscarCategoriasPai();
        List<CategoriaDTO> categoriasDTO = new ArrayList<>();
        
        for (Iterator<Categoria> iterator = categorias.iterator(); iterator.hasNext();) {
			
        	Categoria categoria = iterator.next();
        	
        	logger.debug(categoria.getCategoria());
        	System.out.println(categoria.getCategoria());
        	
        	CategoriaDTO categoriaDTO = new CategoriaDTO();
        	categoriaDTO.setId(categoria.getId());
        	categoriaDTO.setCategoria(categoria.getCategoria());
        	
        	List<Categoria> categoriasCriancas = categoriaService.buscarCategoriasCriancas(categoria.getId());
        	
        	if(categoriasCriancas != null && !categoriasCriancas.isEmpty()) {
        		
        		percorrerCategorias(categoriaDTO, categoriasCriancas.iterator(), "\t");
        	}
        	
        	categoriaDTO.getChildren().sort((s1, s2) -> s1.getCategoria().compareTo(s2.getCategoria()));
        	categoriasDTO.add(categoriaDTO);
        }
        
        return categoriasDTO;
    }
    
    private void percorrerCategorias(CategoriaDTO categoriaDTO, Iterator<Categoria> categoriasCriancas, String tabs) {
			
    		Categoria categoriaCrianca = categoriasCriancas.next();
    		
    		logger.debug(tabs.concat(categoriaCrianca.getCategoria()));
    		System.out.println(tabs.concat(categoriaCrianca.getCategoria()));
    		
    		CategoriaDTO categoriaDTOCrianca = new CategoriaDTO();
    		categoriaDTOCrianca.setId(categoriaCrianca.getId());
    		categoriaDTOCrianca.setCategoria(categoriaCrianca.getCategoria());
        	categoriaDTO.setCaminhoCategoria(categoriaCrianca.getIdCategoriaPai().toString());
    		
    		categoriaDTO.getChildren().add(categoriaDTOCrianca);
    		
    		List<Categoria> categoriasCriancasAux = categoriaService.buscarCategoriasCriancas(categoriaCrianca.getId());
    		
    		if(categoriasCriancasAux != null && !categoriasCriancasAux.isEmpty()) {
    			
    			percorrerCategorias(categoriaDTOCrianca, categoriasCriancasAux.iterator(), "\t".concat("\t"));
    		} 
    		
    		if(categoriasCriancas.hasNext()) {
    			
    			percorrerCategorias(categoriaDTO, categoriasCriancas, "\t");
    		}
		
    }
    
    // POST /api/categorias - Criar nova categoria
    @PostMapping
    public CategoriaDTO criar(@RequestBody CategoriaDTO dto) {
        
    	Categoria categoria = new Categoria();
		
		//categoria.setId(Integer.parseInt(dto.getId().trim()));
		categoria.setCategoria(dto.getCategoria());
		
		if(dto.getCaminhoCategoria() != null && !dto.getCaminhoCategoria().isBlank()) {
    	
			categoria.setIdCategoriaPai(Integer.parseInt(dto.getCaminhoCategoria().trim()));
		}
		
    	categoriaService.salvar(categoria);
    	
    	return dto;
    }
    
    // PUT /api/categorias/{id} - Renomear
    @PutMapping("/{id}")
    public CategoriaDTO renomear(@PathVariable Long id, @RequestBody CategoriaDTO dto) {
        
    	return dto;
    	// Atualiza nome da categoria
    }
    
    // DELETE /api/categorias/{id} - Excluir
    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Integer id) {
        
    	logger.info("Excluída categoria com id {}.", id);
    	
    	categoriaService.excluir(id);
    }
	
}
