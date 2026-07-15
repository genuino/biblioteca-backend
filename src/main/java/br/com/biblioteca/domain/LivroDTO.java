package br.com.biblioteca.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LivroDTO {

	int id;
	String titulo;
	String edicao;
	String editora;
	String descricao;
	String imagem;
	CategoriaDTO categoria;
	String msg;
	int copia;
	List<AutorDTO> autores = new ArrayList<>();
	
}
