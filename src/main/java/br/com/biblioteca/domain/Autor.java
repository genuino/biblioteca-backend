package br.com.biblioteca.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "tbl_autores")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Autor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4235697199357442051L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	@Column(nullable = false, length = 300)
    private String nome;
    
	@Column(nullable = true, length = 400)
	private String foto;
	
	@Column(nullable = true, length = 400)
	private String observacao;
	
	@JsonBackReference
    @ManyToMany(mappedBy = "autores")
    private Set<Livro> livros = new HashSet<>();
}
