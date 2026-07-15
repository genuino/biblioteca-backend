package br.com.biblioteca.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.biblioteca.domain.Autor;
import br.com.biblioteca.domain.Livro;
import br.com.biblioteca.domain.LivroDTO;
import br.com.biblioteca.service.AutorService;
import br.com.biblioteca.service.CategoriaService;
import br.com.biblioteca.service.LivroService;
import br.com.biblioteca.service.MultaService;
import br.com.biblioteca.service.PenalizacaoService;
import br.com.biblioteca.service.ReservaService;
import br.com.biblioteca.service.VendaService;
import br.com.biblioteca.util.Constantes;
import br.com.biblioteca.util.Util;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/biblioteca/livro")
public class LivroController {

	@Autowired
	LivroService livroService;

	@Autowired
	CategoriaService categoriaService;

	@Autowired
	AutorService autorService;

	@Autowired
	ReservaService reservaService;

	@Autowired
	PenalizacaoService penalizacaoService;

	@Autowired
	MultaService multaService;
	
	@Autowired
	VendaService vendaService;
	
    @Value("${upload.path}")
    private String uploadPath;
    
    @Transactional(rollbackFor = Exception.class)
	@PostMapping
	public ResponseEntity<Object> salvarLivro(@RequestBody Livro livro, @RequestParam boolean comRestricao) {
		
		System.out.println("============================================================================================");
		//System.out.println("Livro autor: " + (livro.getAutores() == null ? "autores vazio" : livro.getAutores().toString()));
		System.out.println("Livro id: " + livro.getId());
		System.out.println("============================================================================================");
				
		Map<String, Object> response = new HashMap<>();
		
		Set<Autor> autoresBD = new HashSet<>();
		
		List<String> autores = new ArrayList<>();

		for (Iterator<Autor> iterator = livro.getAutores().iterator(); iterator.hasNext();) {
			
			Autor autorLivro =  iterator.next();
			
			autores.add(autorLivro.getNome());
			
			Autor autorRet =  autorService.buscarNomeAutor(autorLivro.getNome());
						
			if(autorRet != null) {
				
				/*if(autorRet.getLivros() == null) {
					
					autorRet.setLivros(new HashSet<>());
				}
				
				autorRet.getLivros().add(livro);*/
				autoresBD.add(autorRet);
			} else {
				
				//autorLivro.getLivros().add(livro);
				autorLivro.setId(null);
				autoresBD.add(autorService.criar(autorLivro));
			}
		}
		livro.getAutores().clear();
		livro.setAutores(autoresBD);
		
		if(comRestricao) {
			
			autores = livroService.buscarLivroAutor(livro.getTitulo(), autores);
			
			if(autores != null && !autores.isEmpty()) {
				
				String autoresResp = "";
				
				while(!autores.isEmpty()) {
					
					autoresResp = autores.remove(0).concat(",");
				}
				
				autoresResp = autoresResp.substring(0, autoresResp.length() - 1);
				
				response.put("message", "O livro tem o mesmo título e os autores ".concat(autoresResp).concat(". Melhor atualizar a sua quantidade."));
				return ResponseEntity.ofNullable(response);
			}
		}

		System.out.println("============================================================================================");
		System.out.println("Livro categoria: " + (livro.getCategoria() == null ? "categoria vazio" : livro.getCategoria().getCategoria() + " " + livro.getCategoria().getId()));
		System.out.println("============================================================================================");
		
		Livro livroRet = livroService.salvarLivro(livro);
		
		if(livroRet != null) {
			
			String nomeQrCode = "livro_qrCode_".concat(livroRet.getId().toString());
			new Util().criarQRCode(uploadPath, Constantes.MEU_APP_LIVRO.concat(livroRet.getId().toString()), 
					nomeQrCode, Constantes.PNG);
			
			response.put("id", livroRet.getId());
		    response.put("titulo", livroRet.getTitulo());
		    response.put("qrCodeUrl", "http://localhost:8081/images/".
		    		concat(nomeQrCode).concat(".").concat(Constantes.PNG));
		    response.put("message", "Livro cadastrado com sucesso!");
			
			return ResponseEntity.ok(response);
		}
		
		response.put("message", "Cadastro do livro não realizado.");
		return ResponseEntity.ofNullable(response);
	}
	
	// Endpoint to serve image file e se o WebMvcConfigurer não funcionar
    /*@GetMapping("/image")
    public ResponseEntity<Resource> getImage(@RequestParam(name = "id_livro") String idLivro) throws Exception {
        // Path to the image file
        Path path = Paths.get(uploadPath.concat("livro_".concat(idLivro)).
        		concat(".").concat(Constantes.PNG));
        // Load the resource
        Resource resource = new UrlResource(path.toUri());
        // Return ResponseEntity with image content type
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(resource);
    }*/
	
	@GetMapping("/pesquisar_livro")
    public ResponseEntity<List<LivroDTO>> 
		get(@RequestParam(name = "livro") String livro) throws Exception {
		
		System.out.println("=========================================");
	    System.out.println("Parâmetro livro recebido: [" + livro + "]");
	    System.out.println("Tamanho: " + livro.length());
	    System.out.println("=========================================");

		List<Livro> livros = livroService.buscarLivro(livro);

		System.out.println("=========================================");
		System.out.println("Livros.size: " + livros.size());
		System.out.println("=========================================");
		
		List<LivroDTO> livrosDTO = new ArrayList<>();
		for (Livro livroObj : livros) {
			
			new Util(categoriaService);
			LivroDTO livroDTO = Util.formatarLivroParaLivroDTO(livroObj);
			
			livrosDTO.add(livroDTO);
		}
		
		// Return ResponseEntity with image content type
        return ResponseEntity.ok()
                .body(livrosDTO);
    }
	
	@GetMapping("/situacao_livro")
    public ResponseEntity<Map<String, Object>> 
		situacaoLivro(@RequestParam(name = "id_livro") int idLivro,
				@RequestParam(name = "matricula") Integer idAluno,
				@RequestParam(name = "data_inicial") String dataInicial,
				@RequestParam(name = "data_final") String dataFinal,
				@RequestParam(name = "data_final") int tipoPesquisa) throws Exception {
        
		livroService.situacaoLivro(idLivro, dataFinal, idAluno, "", 
				dataInicial, dataFinal, tipoPesquisa);
		
		Map<String, Object> dados = new HashMap<>();
				
		Livro livroObj = livroService.buscarLivroPorId(idLivro);	
			
		dados.put("id", livroObj.getId());
		dados.put("livro", livroObj.getTitulo());
		dados.put("editora", livroObj.getEditora());
		dados.put("edicao", livroObj.getEdicao());
		dados.put("imagem", livroObj.getImagem());
		
		if(livroObj.getCategoria() != null) {
		
			categoriaService.setCaminhoTodaCategoria(livroObj.getCategoria().getCategoria());
			categoriaService.buscarCategoriasTodosPai(livroObj.getCategoria().getIdCategoriaPai());
			dados.put("categoria", categoriaService.getCaminhoTodaCategoria());
		}
		
		Set<Autor> autores = livroObj.getAutores();
		List<String> autoresList = new ArrayList<>();
		for (Autor autor : autores) {
			
			autoresList.add(autor.getNome());
		}
		dados.put("autores", autoresList);
		
		// Return ResponseEntity with image content type
        return ResponseEntity.ok()
                .body(dados);
	}
}
