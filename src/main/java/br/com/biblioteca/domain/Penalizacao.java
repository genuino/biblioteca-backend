package br.com.biblioteca.domain;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_penalizacoes")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Penalizacao implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5608209941419527146L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "data_inicial")
    private LocalDate dataInicial;
    
    @Column(name = "data_final")
    private LocalDate dataFinal;
    
    private String observacao;
    
    @ManyToOne
    @JoinColumn(name = "id_livro_venda")
    private LivroVenda livroVenda;
    
    //@Column(name = "ativa_penalizacao_acumulada")
    //boolean ativaPenalizacaoAcumulada;
    
    private Integer idAluno;
    
    private Integer idFuncionario;
    
    private Integer idEscola;
}
