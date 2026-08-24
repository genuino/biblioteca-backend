package br.com.biblioteca.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.biblioteca.domain.Pagamento;
import br.com.biblioteca.domain.Penalizacao;
import br.com.biblioteca.domain.PenalizacaoDTO;
import br.com.biblioteca.service.PagamentoService;
import br.com.biblioteca.service.PenalizacaoService;
import br.com.biblioteca.util.Util;

@RestController
@RequestMapping("/biblioteca/penalizacoes")
public class PenalizacaoController {

	private static final Logger logger = LogManager.getLogger(VendaController.class);
	
	@Autowired
	PagamentoService pagamentoService;
	
	@Autowired
	PenalizacaoService penalizacaoService;

	  @PostMapping
	  public ResponseEntity<String> penalizacao(@RequestBody PenalizacaoDTO penalizacaoDTO) {
		  
		logger.info("Entrou no cadastro de penalização");
		  
		  try {
			
			String retornoSucesso = "Dados salvo com sucesso!"; 
			
			Penalizacao penalizacao = null;
			if(penalizacaoDTO.getDataInicial() != null) {
				
				penalizacao = Util.toPenalizacao(penalizacaoDTO);
			}
			
			Pagamento pagamento = null;
			if(penalizacaoDTO.getDataVencimento() != null) {
			
				pagamento = Util.toPagamento(penalizacaoDTO);
			}
			
			penalizacaoService.salvar(penalizacao, pagamento);
			
			return  ResponseEntity.ok(
					retornoSucesso);
						
		  } catch (Exception e) {

				//System.out.println("========================================================================");
				//e.printStackTrace();
				//System.out.println("========================================================================");
			  logger.error(e.getMessage());
			  return ResponseEntity.badRequest().build();
			  //return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		  }
		  
	  }
	  
	@GetMapping
	public List<PenalizacaoDTO> buscaPenalizacoes(@RequestParam(name = "paginas") Integer paginas,
		  @RequestParam(name = "idEscola") Integer idEscola) {
	  
		
	  paginas = paginas < 1000 ? 1000 : paginas;
	  List<Penalizacao> penalizacoes = penalizacaoService.buscarPenalizacoes(idEscola, 
			   paginas);
	    
	  List<Pagamento> pagamentos = pagamentoService.buscarPagamentos(idEscola, paginas);
	    
	  return tratarPenalizacoesDTO(penalizacoes, pagamentos);
	}
	
	@GetMapping("/por_campos")
	public List<PenalizacaoDTO> buscaPenalizacoesPorCampos(
			@RequestParam(name = "idEscola") Integer idEscola, 
			@RequestParam(name = "idAluno") Integer idAluno,
			@RequestParam(name = "idLivros") List<Integer> idLivros) {
	  
		
	  List<Penalizacao> penalizacoes = penalizacaoService.buscarPenalizacoesCampos(idEscola, 
			  idAluno, idLivros);
	    
	  List<Pagamento> pagamentos = pagamentoService.buscarpagamentosCampos(idEscola, 
			  idAluno, idLivros);
	  
	  
	  return tratarPenalizacoesDTO(penalizacoes,
				pagamentos);
	}
	
	private List<PenalizacaoDTO> tratarPenalizacoesDTO(List<Penalizacao> penalizacoes,
			List<Pagamento> pagamentos) {
		
		  List<PenalizacaoDTO> penalizacoesDTO = new ArrayList<>();
		  Map<String, Pagamento> pagamentosMap = new HashMap<>();
		  
		  if(pagamentos != null) {
			  
			  pagamentosMap = pagamentos.stream()
				    .collect(Collectors.toMap(
				        p -> p.getObservacao() != null ? p.getObservacao().trim() : "", // Chave com trim e checagem de null
				        p -> p,                                                         // Valor: o próprio objeto
				        (existente, novo) -> novo                                       // Mantém o novo em caso de duplicidade
				    ));
		  }
		  
		  List<Pagamento> pagamentosRemover = new ArrayList<>();
		  
		  for(Penalizacao penalizacao: penalizacoes) {
			  
			  if(!pagamentosMap.isEmpty()) {
				  
				  Pagamento pagamento = pagamentosMap.get(penalizacao.getId().toString());
				  
				  if(pagamento != null) {
				  
					  penalizacoesDTO.add(Util.toDTO(pagamento, 
						  penalizacao));
				  
					  pagamentosRemover.add(pagamento);
				  }
			  } else {
				  
				  penalizacoesDTO.add(Util.toDTO(null, 
						  penalizacao));
			  }
		  }
		  
		  if(!pagamentosRemover.isEmpty()) {
			  pagamentos.removeAll(pagamentosRemover);
			  
			  for(Pagamento pagamento : pagamentos) {
				  
				  penalizacoesDTO.add(Util.toDTO(pagamento, 
						  null));
			  }
		  }
		  
		return penalizacoesDTO; 
	}
}
