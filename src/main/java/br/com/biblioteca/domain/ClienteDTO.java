package br.com.biblioteca.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ClienteDTO implements Serializable {
	
	private static final long serialVersionUID = 2891181918858012190L;

	private Integer id;
	
	private String nome;
    
	private  String telefone;
    
	private String endereco;

    private String numero;
    
    private String complemento;

    private String bairro;
    
    private String cep;
    
    private Integer idCidade;
}
