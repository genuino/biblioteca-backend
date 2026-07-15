package br.com.biblioteca.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_configuracoes")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Configuracao implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6962481389503794365L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	@Column(name = "diaria_emprestimo")
	private Integer diariaEmprestimo;
	
	@Column(name = "qtos_emprestimo")
	private Integer qtosEmprestimo;
	
	@Column(name = "valor_diaria_multa")
	private Double valorDiariaMulta;
	
	@Column(name = "juros_atraso_multa")
	private Double jurosAtrasoMulta;

	@Column(name = "juros_atraso_diario")
	private Double jurosAtrasoDiario;
	
	@Column(name = "qtas_reservas") //Reserva
	private Integer qtasReservas;

	@Column(name = "qtos_dias_reserva")
	private Integer qtosDiasReserva;
	
	@Column(name = "qtas_infracoes_penalizacao")
	private Integer qtasInfracoesPenalizacao;

	@Column(name = "qtos_dias_penalizacao")
	private Integer qtosDiasPenalizacao;
	
	@Column(name = "periodo_penalizacao")
	private Integer periodoPenalizacao;
	
	@Column(name = "cobrar_atraso")
	private Boolean cobrarAtraso;
	
	@Column(name = "id_escola")
	private Integer idEscola;
	
	public Integer getQtosDiasReserva() {
		
		return qtosDiasReserva == null ? 0 : qtosDiasReserva;
	}
}
