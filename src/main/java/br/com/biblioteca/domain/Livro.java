package br.com.biblioteca.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_livros")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Livro implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5796378316124447449L;
	
	/**
	 * Nunca colocar o id com @GeneratedValue, pois preciso do id antes de salvar 
	 * para ciar o QRCode do livro
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(length = 200)
    private String isbn;

    @Column(nullable = false, length = 200)
    private String editora;

    @Column(nullable = false, length = 200)
    private String edicao;
    
    @Column(length = 200)
    private String localizacao;
    
    @Column(columnDefinition = "TEXT")
    private String descricao;
	
    private LocalDate publicacao;

    @JsonManagedReference
    @OneToMany(mappedBy = "livro", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("posicao ASC")
    private List<ImagemLivro> imagens = new ArrayList<>();
    
    @JsonManagedReference
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinTable(
        name = "tbl_livro_autor", 
        joinColumns = @JoinColumn(name = "id_livro"),
        inverseJoinColumns = @JoinColumn(name = "id_autor")
    )
    private Set<Autor> autores = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

	@JsonBackReference
    @ManyToMany(mappedBy = "livros")
    private Set<Penalizacao> penalizacoes = new HashSet<>();
    
	@JsonBackReference
    @ManyToMany(mappedBy = "livros")
    private Set<Pagamento> pagamnetos = new HashSet<>();
	
	private Double valor;
    
    private Integer copia;
    
    private Integer idEscola;
}
