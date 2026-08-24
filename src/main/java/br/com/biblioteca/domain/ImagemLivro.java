package br.com.biblioteca.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_imagens_livro")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ImagemLivro {

	@Id
	@SequenceGenerator(name = "imagem_livro_seq", sequenceName = "imagem_livro_id_seq", allocationSize = 1)
    private Integer id;
	
	@Column(nullable = false, length = 500)
	String caminho;
	
	Integer posicao;
	
	@JsonBackReference
	@ManyToOne
    @JoinColumn(name = "id_livro", nullable = false)
    private Livro livro;
}
