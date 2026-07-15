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
@Table(name = "tbl_multas")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Pagamento implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5769770052196793302L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Double valor;
    
    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

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
