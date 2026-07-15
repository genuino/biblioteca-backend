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
public class ReservaDTO {

	int id;
	String dataInicial;
	String dataFinal;
	Integer idAluno;
	int copia;
	List<LivroDTO> livros = new ArrayList<>();
	ClienteDTO clienteDTO;
}
