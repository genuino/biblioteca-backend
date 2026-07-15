package br.com.biblioteca.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.biblioteca.domain.Livro;
import br.com.biblioteca.domain.LivroReserva;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Integer> {
	
	@Query("SELECT a.nome FROM Livro l LEFT JOIN l.autores a WHERE UPPER(l.titulo) = ?1 AND UPPER(a.nome) IN ?2")
	List<String> findByTitulo(String titulo, List<String> autores);
	
	@Query("SELECT COALESCE(MAX(l.id), 0) FROM Livro l")
	Integer findMaxId();
	
	@Query("SELECT l FROM Livro l JOIN FETCH l.autores a WHERE l.titulo ILIKE ?1 OR a.nome ILIKE ?1 ORDER BY l.titulo LIMIT 10")
	List<Livro> buscarTitulo(String titulo);
	
	@Query("SELECT DISTINCT l FROM Livro l JOIN FETCH l.autores a "
			+ "WHERE (LOWER(l.titulo) LIKE LOWER(?1) OR LOWER(a.nome) LIKE LOWER(?1)) "
			+ "AND l.copia > (SELECT COUNT(v.id) FROM "
			+ "LivroVenda lv JOIN lv.venda v JOIN lv.livro l2 JOIN l2.autores av "
			+ "WHERE lv.dataEntrega IS NULL "
			+ "AND (LOWER(l2.titulo) LIKE LOWER(?1) OR LOWER(av.nome) LIKE LOWER(?1)) GROUP BY v.id) "
			+ "ORDER BY l.titulo ")
	List<Livro> buscarLivrosEmprestimo(String titulo, Pageable pageable);

	@Query("SELECT l FROM LivroReserva lr JOIN lr.reserva r JOIN lr.livro l WHERE r.id = ?1")
	List<Livro> buscarTodasReservas(Integer id);

}
