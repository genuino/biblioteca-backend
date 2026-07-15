package br.com.biblioteca.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.biblioteca.domain.Autor;

@Repository
public interface AutorRepository  extends JpaRepository<Autor, Integer> {
	
	List<Autor> findByNomeContainingIgnoreCase(String nome);
	
	Autor findByNomeIgnoreCase(String nome);
}
