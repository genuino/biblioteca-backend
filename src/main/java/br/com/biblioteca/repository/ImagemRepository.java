package br.com.biblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.biblioteca.domain.ImagemLivro;

public interface ImagemRepository extends JpaRepository<ImagemLivro, Integer> {

	@Query(value = "SELECT nextval('imagem_livro_id_seq')", nativeQuery = true)
    Integer getNextImagemId();
	
	@Query("DELETE FROM ImagemLivro il WHERE il.livro.id=?1")
	void deleteAllImagesLivro(Integer idLivro);
	
}
