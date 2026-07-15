package br.com.biblioteca.domain;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_categorias")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Categoria implements Serializable {
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = -8397565875980632076L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	@Column(nullable = false, length = 100)
	private String categoria;
	
	@Column(name = "id_categoria_pai")
	private Integer idCategoriaPai;
	
	public String toString() {
		
		return categoria;
	}
}
