package br.com.biblioteca.domain;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class VendaDTO {
	
	private Integer id;
    private Double valor;
    private LocalDate data;
    
    private LocalDate dataEntrega;
    
    private Cliente cliente;
    
    private Set<LivroDTO> livros = new HashSet<>();
    
    private Integer copia;
    private Boolean emprestimo;
    
    private Integer idAluno;
    
    private Integer idFuncionario;
    
}
