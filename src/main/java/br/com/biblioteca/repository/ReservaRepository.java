package br.com.biblioteca.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.biblioteca.domain.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
	
	@Query("SELECT r.dataInicial, lr.dataFinal FROM LivroReserva lr JOIN lr.reserva r JOIN lr.livro l "
			+ "WHERE lr.dataFinal >= CURRENT_DATE AND l.id = ?1 AND r.idAluno <> ?2")
	List<LocalDate[]> findByCopia(int idLivro, Integer idAluno);

	@Query("SELECT l.copia - COUNT(l.id) FROM LivroReserva lr JOIN lr.reserva r JOIN lr.livro l "
			+ "WHERE l.id IN ?1 AND r.dataInicial >= ?2 AND lr.dataFinal <= ?3 GROUP BY l.id, l.copia")
	Integer findByCopiaQuantosReservasLivroData(int idLivro,  LocalDate dataInicial, LocalDate dataFinal);
	
	@Query(value = "SELECT (c.qtas_reservas - COUNT(rl.data_final)) AS status "
	        + "FROM tbl_configuracoes c, tbl_reservas r "
	        + "JOIN tbl_livro_reserva rl ON rl.id_reserva = r.id "
	        + "JOIN tbl_livros l ON l.id = rl.id_livro "
	        + "WHERE rl.data_final >= CURRENT_DATE "
	        + "AND (r.id_cliente = ?2 OR r.id_aluno = ?1) GROUP BY c.qtas_reservas, rl.data_final",
	        nativeQuery = true)
	Integer findByCopiaQuantosReservas(Integer idAluno, int idCliente);
	
	@Query("SELECT r FROM LivroReserva lr JOIN lr.reserva r WHERE lr.dataFinal >= CURRENT_DATE ")
	List<LocalDate[]> buscarReservas();
	
}
