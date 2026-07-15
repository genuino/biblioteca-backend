package br.com.biblioteca.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.biblioteca.domain.Configuracao;
import br.com.biblioteca.domain.Livro;
import br.com.biblioteca.domain.LivroDTO;
import br.com.biblioteca.domain.LivroVenda;
import br.com.biblioteca.domain.Venda;
import br.com.biblioteca.domain.VendaDTO;
import br.com.biblioteca.service.CategoriaService;
import br.com.biblioteca.service.ConfiguracaoService;
import br.com.biblioteca.service.MultaService;
import br.com.biblioteca.service.PenalizacaoService;
import br.com.biblioteca.service.ReservaService;
import br.com.biblioteca.service.VendaService;
import br.com.biblioteca.util.Util;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/biblioteca/venda")
public class VendaController {

	private static final Logger logger = LogManager.getLogger(VendaController.class);
	
	@Autowired
	MultaService multaService;
	
	@Autowired
	PenalizacaoService penalizacaoService;

	@Autowired
	ReservaService reservaService;

	@Autowired
	VendaService vendaService;
	
	@Autowired
	ConfiguracaoService configuracaoService;

	@Autowired
	CategoriaService categoriaService;
	
	  @PostMapping
	  public ResponseEntity<String> emprestimo(@RequestBody VendaDTO vendaDTO) {
		  
		logger.info("Entrou no empréstimo");
		  
		  try {
			
			// ✅ Coleta os livros a remover e remove depois do loop
			Set<Livro> livrosParaBD = new HashSet<>();
					
			Configuracao config = configuracaoService.buscarConfiguracao();
						  
			String retornoErro = "";
			String retornoSucesso = "";
			
			logger.info("Quantidade de livros: '{}'", vendaDTO.getLivros() == null ? 0 : vendaDTO.getLivros().size());  
			
			Venda venda = Util.toEntity(vendaDTO);
			for(LivroDTO livro: vendaDTO.getLivros()) {
			
				boolean adicionarLivro = true;
								
				Integer qtosLivros = vendaService.buscarLivroEmprestado(livro.getId(), vendaDTO.getIdAluno());  
				
				if(qtosLivros <= 0 ) {

					logger.info("Buscou quantos livros emprestados ao cliente: {}", qtosLivros);
					
					retornoErro = retornoErro.concat(" Limite de empréstimos de livros excedidos.\n");
					
					adicionarLivro = false;
				}
				
				qtosLivros = vendaService.buscarMesmoLivroEmprestado(livro.getId(), vendaDTO.getIdAluno());  
				
				if(qtosLivros > 0 ) {

					logger.info("Buscou quantos livros emprestados: {}", qtosLivros);
					
					retornoErro = retornoErro.concat("Livro "
							.concat(livro.getTitulo()).concat(" já emprestado para o usuário.\n"));
					
					adicionarLivro = false;
				}
				
				int retorno = multaService.clienteMultado(vendaDTO.getIdAluno(),
						vendaDTO.getCliente() != null ? vendaDTO.getCliente().getId() : -1);  
				
				if(retorno < 0) {

					logger.info("Buscou quantas multas ao cliente: {}", retorno);
					
					retornoErro = retornoErro.concat("O aluno tem ".concat(Integer.toString(retorno).replace("-", ""))
						.concat(" multa(s). Impossibilitando de realizar empréstimo.\n"));
					
					adicionarLivro = false;
				} 
					
				LocalDate dataFinal = penalizacaoService.clientePenalizado(vendaDTO.getIdAluno(),
						vendaDTO.getCliente() != null ? vendaDTO.getCliente().getId() : -1);

				if(dataFinal != null) {

					logger.info("Buscou penalização ao cliente: {}", dataFinal.toString());
					
					retornoErro = retornoErro.concat("O aluno tem ".concat(Integer.toString(retorno).replace("-", ""))
						.concat(" penalização(ões). Impossibilitando de realizar empréstimo.\n"));
					
					adicionarLivro = false;
					
				} 
						
				Integer qtasResevas = reservaService.verificarReservaDatas(livro.getId(), 
						LocalDate.now().minusDays(config.getQtosDiasReserva()), LocalDate.now());

				if(qtasResevas <= 0) {

					logger.info("Buscar quantas reservas dos livros: {}", qtasResevas);
					
					String datas = reservaService.livroReservado(livro.getId(), 
							vendaDTO.getIdAluno());
					
					if(datas != null && !datas.isBlank() 
						&& !datas.contains(Util.formatLocalDatetoString(LocalDate.now()))) {

						logger.info("Buscar datas das reservas do cliente: {}", datas);
						
						retornoErro = retornoErro.concat("Livro ")
							.concat(livro.getTitulo())
							.concat(" reservado nas datas ")
							.concat(datas.substring(0, datas.length() - 1))
							.concat(" Impossibilitando de realizar o empréstimo.\n");

						adicionarLivro = false;
					}
						
				}
				
				if(adicionarLivro) {
					
					logger.info("Livro será emprestado: {}", livro.getTitulo());
					livrosParaBD.add(Util.toEntity(livro));
					retornoSucesso = retornoSucesso.concat("Empréstimo realizado com sucesso para o livro "
							.concat(livro.getTitulo()).concat("!\n"));
				} else {
				
					logger.info("Livro não será emprestado: {}", livro.getTitulo());
					
				}

			}	

			// Remove fora do loop
			logger.info("Quantidade de livros emprestados: {}", livrosParaBD.size());
						
			if(livrosParaBD != null && !livrosParaBD.isEmpty()) {
				
				logger.info("Quantidade de livros emprestados: {}", livrosParaBD.size());
				
				vendaService.salvarVenda(livrosParaBD, venda);
				
			}
			  
			return retornoErro.isBlank() ? ResponseEntity.ok(
					retornoSucesso.substring(0, retornoSucesso.length() -2)) : 
				ResponseEntity.badRequest().body(retornoSucesso.concat(retornoErro.substring(0, retornoErro.length()-2)));
						
		  } catch (Exception e) {

				System.out.println("========================================================================");
				e.printStackTrace();
				System.out.println("========================================================================");
			  logger.error(e.getMessage());
			  return ResponseEntity.badRequest().build();
			  //return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		  }
		  
	  }
	  
	  @GetMapping
	  public List<VendaDTO> buscaEmprestimos(@RequestParam(name = "paginas") int paginas,
			  @RequestParam(name = "idAluno", required = false) Integer idAluno, 
			  @RequestParam(name = "ids", required = false) List<Integer> ids) {
		  
		  System.out.println("====================================================================");
		  System.out.println("ENTROU");
		  System.out.println("====================================================================");
		  
		  List<LivroVenda> livroVendas = null;
		  if((idAluno == null) 
				  && (ids == null || ids.isEmpty())) {
			  
			  livroVendas = vendaService.buscarLivroEmprestado(paginas < 1000 ? 1000 : paginas );
		  } else {
			  
			  livroVendas = vendaService.buscarLivroEmprestado(ids, idAluno);
			  
		  }
		  
		  Map<Integer, VendaDTO> vendasDTO = new HashMap<>();
		  livroVendas.stream()
				    .forEach(livroVenda -> {
				        
				    	new Util(categoriaService);
		            	LivroDTO livroDto = Util.formatarLivroParaLivroDTO(livroVenda.getLivro());
				    	
		            	Set<LivroDTO> livrosDTO = new HashSet<>();
		            	if(vendasDTO.get(livroVenda.getId()) != null) {
		            		
		            		livrosDTO = vendasDTO.get(livroVenda.getId()).getLivros();
		            	} 
		            	
		            	livrosDTO.add(livroDto);
		            	
				    	VendaDTO dto = new VendaDTO(livroVenda.getVenda().getId(), 
				    			livroVenda.getVenda().getValor(), livroVenda.getVenda().getData(), 
				    			livroVenda.getDataEntrega(), livroVenda.getVenda().getCliente(),
				        		livrosDTO, livroVenda.getVenda().getCopia(), livroVenda.getVenda().getEmprestimo(), 
				        		livroVenda.getVenda().getIdAluno(), livroVenda.getVenda().getIdFuncionario());
				        
				    	vendasDTO.put(livroVenda.getVenda().getId(), dto);
				    	
				    });
		  
		  return new ArrayList<>(vendasDTO.values());
	  }
	  
	  @PatchMapping("/{idEmprestimo}")
	  public ResponseEntity<String> devolverLivro(
	          @PathVariable Integer idEmprestimo) {
		  
		  logger.info("Entrou na devolução de livro");

		  String retorno = "Devolução realizada com sucesso!";
	      LivroVenda livroVenda = vendaService.findByLivroVenda(idEmprestimo);
		  
		  try {
			  
			  Map<String, Object> campos = new HashMap<>();
			  
		      LocalDate dataEntrega = LocalDate.now();
		      campos.put("dataEntrega", dataEntrega);
		      livroVenda.setDataEntrega(dataEntrega);
		      
		      Configuracao config = configuracaoService.buscarConfiguracao();
		      
		      logger.info("Data do empréstimo: {}", livroVenda.getVenda().getData().toString());
		      logger.info("Data da entrega: {}", livroVenda.getDataEntrega().toString());
		      
		      long dias = ChronoUnit.DAYS.between(livroVenda.getVenda().getData(), livroVenda.getDataEntrega());
		      
		      Long atraso = dias - config.getDiariaEmprestimo().longValue();
		      
		      if(atraso > 0) {
		    	  
		    	  if(config.getCobrarAtraso()) {
		    		  
		    		  retorno = "\nAtrasado ".concat(
		    				  Long.toString(atraso)).concat(" dia(s).");
		    		  atraso = -atraso;
		    		  
		    	  }
		      } else {
		    	  
		    	  atraso = 0l;
		      }
		      
		      Venda vendaAtualizada = vendaService.atualizarParcial(livroVenda, campos,
		    		  atraso.intValue(), config);
		      
		      if(vendaAtualizada != null) {
		    	  
		    	  if(atraso >= 0) {
		    		  
		    		  return ResponseEntity.ok(retorno);
		    	  }
		    	  else {
		    		
		    		  return ResponseEntity.badRequest().body(retorno);
		    	  }
		          
		      }
		    
		      return ResponseEntity.badRequest().body("Devolução não realizada. Tente Novamente.");
		      
		  } catch(Exception ex) {
			  
			  ex.printStackTrace();
			  return ResponseEntity.badRequest().body(ex.getMessage());
		  }
	  }
}
