package br.com.biblioteca.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.biblioteca.domain.Penalizacao;

@Repository
public interface PenalizacaoRepository extends JpaRepository<Penalizacao, Integer>  {

	@Query("SELECT p.dataFinal as dataFinal "
		+ "FROM Penalizacao p WHERE p.dataFinal > CURRENT_DATE "
		+ "AND (p.livroVenda.venda.cliente.id = ?2 OR p.livroVenda.venda.idAluno = ?1)")
	LocalDate findByDataFinal(Integer idAluno, int idCliente);
	
	@Query("SELECT COUNT(p.dataFinal) FROM Penalizacao p " +
		       "WHERE p.dataFinal > CURRENT_DATE " +
		       "AND p.dataInicial >= :dataLimite " +
		       "AND (p.livroVenda.venda.cliente.id = :idCliente OR p.livroVenda.venda.idAluno = :idAluno OR p.livroVenda.venda.idFuncionario = :idFuncionario) "
		     + "GROUP BY p.dataFinal")
	Integer buscarPenalizacaoAtiva(
		    @Param("idAluno") Integer idAluno,
		    @Param("idCliente") Integer idCliente,
		    @Param("idFuncionario") Integer idFuncionario,
		    @Param("dataLimite") LocalDate dataLimite);
}
