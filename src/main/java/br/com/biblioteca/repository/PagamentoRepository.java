package br.com.biblioteca.repository;

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
}
