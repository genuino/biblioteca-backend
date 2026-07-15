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
@Table(name = "tbl_vendas")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Venda implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1635799196961226393L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Double valor;
    private LocalDate data;
    
    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;
    
    private Integer copia;
    private Boolean emprestimo;
    
    @Column(name = "id_aluno")
    private Integer idAluno;

    @Column(name = "id_funcionario")
    private Integer idFuncionario;
    
}
