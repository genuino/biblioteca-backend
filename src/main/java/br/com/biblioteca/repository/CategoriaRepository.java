package br.com.biblioteca.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.biblioteca.domain.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

	List<Categoria> findByIdCategoriaPaiOrderByCategoriaDesc(Integer idCategoriaPai);
	
	@Query("SELECT c FROM Categoria c WHERE c.idCategoriaPai IS NULL ORDER BY c.categoria")
	List<Categoria> findByIdCategoriaPai();
	
	Optional<Categoria> findByCategoria(String categoria);

	@Query("SELECT c FROM Categoria c WHERE c.id = ?1")
	Categoria buscarCategoriasPai(Integer idCategoriaPai);
}
