package br.com.biblioteca.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.biblioteca.domain.Venda;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Integer> {
	
	@Query("SELECT COALESCE(SUM(v.copia), 0) - l.copia FROM LivroVenda lv JOIN lv.venda v "
			+ "JOIN lv.livro l "
			+ "WHERE l.id = ?1 AND lv.dataEntrega IS NULL AND v.emprestimo = TRUE "
			+ " GROUP BY l.copia, v.copia")
	Integer buscarQtosLivroBiblioteca(Integer id);

	@Query(value = "select "
			+ "    coalesce(sum(v1_0.copia), 0) "
			+ "from "
			+ "    tbl_vendas v1_0 "
			+ "join "
			+ "    tbl_livro_venda l1_0 on v1_0.id = l1_0.id_venda "
			+ "where "
			+ "    l1_0.id_livro = ?1 "
			+ "    and l1_0.data_entrega is null "
			+ "    and v1_0.emprestimo = true "
			+ "    and v1_0.id_aluno = ?2 ", nativeQuery = true)
	Integer buscarMesmoLivroEmprestado(Integer id, Integer idAluno);
	
	@Query(value = "select "
			+ "    (select c.qtos_emprestimo from tbl_configuracoes c limit 1) - coalesce(sum(v1_0.copia), 0) "
			+ "from "
			+ "    tbl_vendas v1_0 "
			+ "join "
			+ "    tbl_livro_venda l1_0 on v1_0.id = l1_0.id_venda "
			+ "where "
			+ "    l1_0.id_livro = ?1 "
			+ "    and l1_0.data_entrega is null "
			+ "    and v1_0.emprestimo = true "
			+ "    and v1_0.id_aluno = ?2 ", nativeQuery = true)
	Integer buscarLivroEmprestado(Integer id, Integer idAluno);
	
	// Primeiro busca os IDs paginados
	@Query("SELECT v.id FROM LivroVenda lv JOIN lv.venda v "
	     + "WHERE lv.dataEntrega IS NULL AND v.emprestimo = TRUE "
	     + "ORDER BY v.data")
	List<Integer> buscarIdsEmprestados(Pageable pageable);

}
