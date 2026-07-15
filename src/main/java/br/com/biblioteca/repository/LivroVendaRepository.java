package br.com.biblioteca.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.biblioteca.domain.LivroVenda;
import br.com.biblioteca.domain.Venda;

@Repository
public interface LivroVendaRepository extends JpaRepository<LivroVenda, Integer>  {
	
	@Query("SELECT lv FROM LivroVenda lv JOIN lv.venda v JOIN lv.livro l "
			+ "WHERE l.id = ?1 AND v.id = ?2")
	LivroVenda findByLivroVenda(Integer idLivro, Integer idVenda);

	@Query("SELECT lv FROM LivroVenda lv JOIN lv.venda v JOIN lv.livro l "
			+ "WHERE v.id = ?1")
	List<LivroVenda> findByVenda(Integer idVenda);

	// Depois carrega com FETCH usando os IDs
	@Query("SELECT DISTINCT lv FROM LivroVenda lv JOIN FETCH lv.venda v "
	     + "JOIN FETCH lv.livro l "
	     + "JOIN FETCH l.autores a "
	     + "WHERE v.id IN :ids ")
	List<LivroVenda> buscarVendasComLivrosEAutores(@Param("ids") List<Integer> ids);

	@Query("SELECT DISTINCT lv FROM LivroVenda lv JOIN lv.venda v JOIN FETCH lv.livro l "
			+ "JOIN FETCH l.autores a "
			+ "WHERE lv.dataEntrega IS NULL AND v.emprestimo = TRUE "
			+ "AND (l.id IN ?1 OR v.idAluno = ?2)"
			+ "ORDER BY v.data")
	List<LivroVenda> buscarLivroEmprestadoPorMatricula(List<Integer> ids, Integer idAluno);
}
