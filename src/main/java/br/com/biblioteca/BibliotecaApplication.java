package br.com.biblioteca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(scanBasePackages = "br.com.biblioteca")
//@EntityScan("br.com.biblioteca.domain")  // ⬅️ Escanear entidades
//@EnableJpaRepositories("br.com.biblioteca.repository")
public class BibliotecaApplication {

	public static void main(String[] args) {
		
		ApplicationContext context = SpringApplication.run(BibliotecaApplication.class, args);
		
		/*CategoriaService categoriaService = context.getBean(CategoriaService.class);
			
		Categoria categoria = new Categoria();
		categoria.setCategoria("Ficção");
		
		Categoria categoriaRet = categoriaService.salvar(categoria);
		Integer idCategoriaPai = categoriaRet.getId();
		
		categoria = new Categoria();
		categoria.setCategoria("Romance");
		categoria.setIdCategoriaPai(idCategoriaPai);
		
		categoriaService.salvar(categoria);
		
		categoria = new Categoria();
		categoria.setCategoria("Suspense");
		categoria.setIdCategoriaPai(idCategoriaPai);
		
		categoriaService.salvar(categoria);
		
		categoria = new Categoria();
		categoria.setCategoria("Fantasia");
		categoria.setIdCategoriaPai(idCategoriaPai);
		
		categoriaService.salvar(categoria);
		
		categoria = new Categoria();
		categoria.setCategoria("Ficção científica");
		categoria.setIdCategoriaPai(idCategoriaPai);
		
		categoriaService.salvar(categoria);
		
		

		categoria = new Categoria();
		categoria.setCategoria("Não-ficção");
		
		categoriaRet = categoriaService.salvar(categoria);
		idCategoriaPai = categoriaRet.getId();
		
		categoria = new Categoria();
		categoria.setCategoria("Biografia");
		categoria.setIdCategoriaPai(idCategoriaPai);
		
		categoriaService.salvar(categoria);
		
		categoria = new Categoria();
		categoria.setCategoria("História");
		categoria.setIdCategoriaPai(idCategoriaPai);
		
		categoriaService.salvar(categoria);
		
		categoria = new Categoria();
		categoria.setCategoria("Ciência");
		categoria.setIdCategoriaPai(idCategoriaPai);
		
		categoriaService.salvar(categoria);
				

		

		categoria = new Categoria();
		categoria.setCategoria("Técnico");

		categoriaRet = categoriaService.salvar(categoria);
		idCategoriaPai = categoriaRet.getId();
		
		categoria = new Categoria();
		categoria.setCategoria("Programação");
		categoria.setIdCategoriaPai(idCategoriaPai);
		
		categoriaService.salvar(categoria);
		
		categoria = new Categoria();
		categoria.setCategoria("Engenharia");
		categoria.setIdCategoriaPai(idCategoriaPai);
		
		categoriaService.salvar(categoria);
		
		categoria = new Categoria();
		categoria.setCategoria("Medicina");
		categoria.setIdCategoriaPai(idCategoriaPai);
		
		categoriaService.salvar(categoria);
		

		

		categoria = new Categoria();
		categoria.setCategoria("Educacional");

		categoriaRet = categoriaService.salvar(categoria);
		idCategoriaPai = categoriaRet.getId();
		
		categoria = new Categoria();
		categoria.setCategoria("Infantil");
		categoria.setIdCategoriaPai(idCategoriaPai);
		
		categoriaService.salvar(categoria);
		
		categoria = new Categoria();
		categoria.setCategoria("Ensino Fundamental");
		categoria.setIdCategoriaPai(idCategoriaPai);
		
		categoriaService.salvar(categoria);
		
		categoria = new Categoria();
		categoria.setCategoria("Ensino Médio");
		categoria.setIdCategoriaPai(idCategoriaPai);
		
		categoriaService.salvar(categoria);


		

		categoria = new Categoria();
		categoria.setCategoria("Outros");
		
		categoriaService.salvar(categoria);*/
		
	}

}
