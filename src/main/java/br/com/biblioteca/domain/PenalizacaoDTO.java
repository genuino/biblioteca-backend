package br.com.biblioteca.domain;

import java.time.LocalDate;
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
public class PenalizacaoDTO {
	
	private Integer idPagamento;
    
	private Double valor;
    
    private LocalDate dataVencimento;

    private LocalDate dataPagamento;

    private String observacaoPagamento;
    
    private Integer idLivroVenda;
    
    private Integer idAluno;
    
    private Integer idFuncionario;
    
    private String pessoa;
    
    private Integer idEscola;
    
    private String escola;
    
    private Integer idPenalizacao;
    
    private LocalDate dataInicial;
    
    private LocalDate dataFinal;
    
    private String observacaoPenalizacao;
    
    private List<LivroDTO> livrosDTO = new ArrayList<>();
    
    private Integer cobrarJuros;
}
