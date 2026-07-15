package br.com.biblioteca.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_clientes")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Cliente implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5049925255187575608L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	@Column(nullable = false, length = 200)  // ⬅️ ADICIONADO
    private String nome;
    
	@Column(length = 20)  // ⬅️ ADICIONADO
    private  String telefone;
    
	@Column(length = 300)  // ⬅️ ADICIONADO
    private String endereco;

    @Column(length = 20)  // ⬅️ ADICIONADO
    private String numero;
    
    @Column(length = 20)  // ⬅️ ADICIONADO
    private String complemento;

    @Column(length = 100)  // ⬅️ ADICIONADO
    private String bairro;
    
    @Column(length = 12)  // ⬅️ CORRIGIDO
    private String cep;
    
    @Column(name = "id_cidade")
    private Integer idCidade;

}