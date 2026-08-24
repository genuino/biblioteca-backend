package br.com.biblioteca.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.biblioteca.domain.Pagamento;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Integer> {

	@Query(value = "SELECT CASE WHEN (select c.qtas_infracoes_penalizacao from tbl_configuracoes c limit 1) "
			+ " < COALESCE(COUNT(m.id), 0) "
	        + "THEN -COUNT(m.id) "
	        + "ELSE 1 END AS status FROM tbl_multas m "
	        + "JOIN tbl_livro_venda lv ON lv.id = m.id_livro_venda "
	        + "JOIN tbl_vendas v ON v.id = lv.id_venda "
	        + "JOIN tbl_clientes cli ON cli.id = v.id_cliente "
	        + "WHERE m.data_pagamento IS NULL "
	        + "AND (cli.id = ?2 OR v.id_aluno = ?1) ",
	        nativeQuery = true)
	Integer findByDataPagamento(Integer idAluno, int idCliente);
	
	@Query("SELECT m "
			+ "FROM Pagamento m WHERE m.dataPagamento IS NULL "
			+ "AND m.idEscola = ?1")
	List<Pagamento> buscarPagamentos(Integer idEscola, Pageable pageable);

	@Query("SELECT DISTINCT m "
		+ "FROM Pagamento m JOIN m.livros l WHERE m.dataPagamento IS NULL "
		+ "AND m.idEscola = ?1 AND (m.idAluno = ?2 OR l.id IN (?3))")
	List<Pagamento> buscarPagamentosCampos(Integer idEscola, Integer idAluno, List<Integer> idLivros);

}
