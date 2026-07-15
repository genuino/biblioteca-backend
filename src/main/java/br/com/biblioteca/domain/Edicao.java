package br.com.biblioteca.domain;

import java.io.Serializable;

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
@Table(name = "tbl_edicao")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Edicao implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5117192931813712673L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;
	
	String edicao;
	
	@ManyToOne
    @JoinColumn(name = "id_livro", nullable = false)
	Livro livro;
	
}
