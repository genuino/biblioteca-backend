package br.com.biblioteca.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.biblioteca.controller.VendaController;
import br.com.biblioteca.domain.Categoria;
import br.com.biblioteca.repository.CategoriaRepository;

@Service
public class CategoriaService {

	private static final Logger logger = LogManager.getLogger(CategoriaService.class);
	
	@Autowired
    private CategoriaRepository categoriaRepository;
	
	private String caminhoTodaCategoria;
	
	public List<Categoria> buscarCategoriasCriancas(int idCategoriaPai) {
		
		return categoriaRepository.findByIdCategoriaPaiOrderByCategoriaDesc(idCategoriaPai);
	}
	
	public List<Categoria> buscarCategoriasPai() {
		
		return categoriaRepository.findByIdCategoriaPai();
	}

	public List<Categoria> buscarCategoriasTodosPai(int idCategoriaPai) {
		
		List<Categoria> dados = new ArrayList<>();
		
		while(true) {
			
			logger.info("Id da categoria: " + idCategoriaPai);
			
			Categoria categoria = 
				categoriaRepository.buscarCategoriasPai(idCategoriaPai);

			caminhoTodaCategoria = 
				categoria.getCategoria().concat(" - ").concat(caminhoTodaCategoria);
						
			logger.info("Montando categoria: " + caminhoTodaCategoria);
			
			dados.add(categoria);	

			if(categoria.getIdCategoriaPai() == null) {
				
				break;
			} 
			
			idCategoriaPai = categoria.getIdCategoriaPai();
		}
		
		return dados;
	}
	
	@Transactional
	public Categoria salvar(Categoria categoria) {
		
		Optional<Categoria> categoriaRet = categoriaRepository.findByCategoria(categoria.getCategoria());
		
		if(categoriaRet.isPresent()) {
			
			return categoriaRet.get();
		}
		
		return categoriaRepository.save(categoria);
	}
	
	public void excluir(Integer id) {
		
		List<Categoria> categoriasCriancas = buscarCategoriasCriancas(id);
		
		if(categoriasCriancas != null && !categoriasCriancas.isEmpty()) {
			
			categoriaRepository.flush();
			categoriaRepository.deleteAllInBatch(categoriasCriancas);
		}
		
		categoriaRepository.deleteById(id);
		
		
	}

	public String getCaminhoTodaCategoria() {
		return caminhoTodaCategoria;
	}

	public void setCaminhoTodaCategoria(String caminhoTodaCategoria) {
		this.caminhoTodaCategoria = caminhoTodaCategoria;
	}
}
