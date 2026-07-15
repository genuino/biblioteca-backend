package br.com.biblioteca.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.biblioteca.domain.Configuracao;
import br.com.biblioteca.service.ConfiguracaoService;
import br.com.biblioteca.util.Constantes;

@RestController
@RequestMapping("/biblioteca/configuracao")
public class ConfiguracaoController {
	
	@Autowired
    private ConfiguracaoService configuracaoService;

	@PostMapping
	public ResponseEntity<String> salvarConfiguracao(@RequestBody Map<String, Double> confs) throws Exception {
		
		//String retorno = "";
		
		if(confs != null && !confs.isEmpty()) {
			
			Configuracao configuracao = new Configuracao();
			
			if(confs.get("id") != null && confs.get("id").intValue() != -1) {
				
				configuracao.setId(confs.get("id").intValue());
			}
				
			configuracao.setDiariaEmprestimo(confs.get("diariaEmprestimo").intValue());
			configuracao.setQtosEmprestimo(confs.get("qtosEmprestimo").intValue());
			configuracao.setJurosAtrasoDiario(confs.get("jurosAtrasoDiario"));
			configuracao.setJurosAtrasoMulta(confs.get("jurosAtrasoMulta"));
			configuracao.setQtosDiasReserva(confs.get("qtosDiasReserva").intValue());
			configuracao.setQtosDiasPenalizacao(confs.get("qtosDiasPenalizacao").intValue());
			configuracao.setQtasInfracoesPenalizacao(confs.get("qtasInfracoesPenalizacao").intValue());
			configuracao.setQtasReservas(confs.get("qtasReservas").intValue());
			configuracao.setValorDiariaMulta(confs.get("valorDiariaMulta"));
			configuracao.setPeriodoPenalizacao(confs.get("periodoPenalizacao").intValue());               
			
			System.out.println("==========1============");
			System.out.println(confs.get("periodoPenalizacao").intValue());
			System.out.println("======================");
			
			configuracaoService.salvar(configuracao);
			
			return ResponseEntity.ok("Configuração salva com sucesso!");
		}
		
		return ResponseEntity.badRequest().body("Dados inválidos!");
	}
			
	@GetMapping("/pesquisar_conf")
    public ResponseEntity<Map<String, Double>> get(@RequestParam int tipoConfiguracao) throws Exception {
        
		Configuracao configuracao = configuracaoService.buscarConfiguracao();
		
		Map<String, Double> dados = new HashMap<>();
		switch (tipoConfiguracao) {
			
			case Constantes.CONF_RESERVA: {
				
				dados.put("id", configuracao.getId().doubleValue());
				dados.put("qtosDiasReservas", configuracao.getQtosDiasReserva().doubleValue());
				dados.put("qtasReservas", configuracao.getQtasReservas().doubleValue());
				
				break;
			}
			default: {
				
				dados.put("id", configuracao.getId().doubleValue());
				dados.put("qtosDiasReserva", configuracao.getQtosDiasReserva().doubleValue());
				dados.put("qtasReservas", configuracao.getQtasReservas().doubleValue());
				dados.put("diariaEmprestimo", configuracao.getDiariaEmprestimo().doubleValue());
				dados.put("qtosEmprestimo", configuracao.getQtosEmprestimo().doubleValue());
				dados.put("jurosAtrasoDiario", configuracao.getJurosAtrasoDiario());
				dados.put("jurosAtrasoMulta", configuracao.getJurosAtrasoMulta());
				dados.put("qtosDiasPenalizacao", configuracao.getQtosDiasPenalizacao().doubleValue());
				dados.put("qtasInfracoesPenalizacao", configuracao.getQtasInfracoesPenalizacao().doubleValue());
				dados.put("valorDiariaMulta", configuracao.getValorDiariaMulta());
				dados.put("periodoPenalizacao", configuracao.getPeriodoPenalizacao().doubleValue());
				
				System.out.println("==========2============");
				System.out.println(configuracao.getPeriodoPenalizacao());
				System.out.println("======================");
				
				break;
			}
					
		}
		
		// Return ResponseEntity with image content type
        return ResponseEntity.ok()
                .body(dados);
    }
}
