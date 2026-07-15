package br.com.biblioteca.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.biblioteca.domain.LivroReserva;
import br.com.biblioteca.domain.Reserva;

public interface LivroReservaRepository extends JpaRepository<LivroReserva, Integer>  {
	
	@Query("SELECT lr FROM LivroReserva lr JOIN lr.reserva r WHERE lr.dataFinal >= CURRENT_DATE")
	List<LivroReserva> buscarTodasReservas(Pageable page);

	@Query("SELECT DISTINCT lr FROM LivroReserva lr LEFT JOIN lr.reserva r "
		    + "LEFT JOIN lr.livro l "
		    + "WHERE "
		    + "    ("
		    + "        r.dataInicial IS NOT NULL AND lr.dataFinal IS NOT NULL "
		    + "        AND r.dataInicial >= :dataInicial "
		    + "        AND lr.dataFinal <= :dataFinal"
		    + "    ) "
		    + "    OR "
		    + "    ("
		    + "        (l.id IN :ids OR r.idAluno IN :nomes) "
		    + "    )")
	List<LivroReserva> buscarReservasPorNomesDatas(List<Integer> ids, String matricula, 
			String dataInicial, String dataFinal);

	@Query("SELECT lr FROM LivroReserva lr JOIN lr.reserva r JOIN lr.livro l "
			+ "WHERE lr.dataFinal >= CURRENT_DATE AND (l.id IN :ids OR r.idAluno IN :nomes)")
	List<LivroReserva> buscarReservasPorNomes(List<Integer> nomes, List<Integer> ids);

}
