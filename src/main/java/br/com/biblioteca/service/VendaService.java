package br.com.biblioteca.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.biblioteca.domain.Configuracao;
import br.com.biblioteca.domain.Livro;
import br.com.biblioteca.domain.LivroVenda;
import br.com.biblioteca.domain.Pagamento;
import br.com.biblioteca.domain.Penalizacao;
import br.com.biblioteca.domain.Venda;
import br.com.biblioteca.repository.LivroRepository;
import br.com.biblioteca.repository.LivroVendaRepository;
import br.com.biblioteca.repository.PenalizacaoRepository;
import br.com.biblioteca.repository.VendaRepository;
import br.com.biblioteca.util.Constantes;

@Service
public class VendaService implements Constantes {

	@Autowired
	VendaRepository vendaRepository;

	@Autowired
	LivroRepository livroRepository;

	@Autowired
	LivroVendaRepository livroVendaRepository;

	@Autowired
	PenalizacaoRepository penalizacaoRepository;

	@Transactional
	public void salvarVenda(Set<Livro> livros, Venda venda) {
	    
		Venda vendaSalva = vendaRepository.save(venda);
		
		for (Livro livro : livros) {
			
	        LivroVenda livroVenda = new LivroVenda();  // 👈 nova instância por livro!
	        livroVenda.setVenda(vendaSalva);
	        livroVenda.setLivro(livro);
	        livroVendaRepository.save(livroVenda);
	    }
	}
	
	@Transactional
	public Venda atualizarParcial(LivroVenda livroVenda, Map<String, Object> campos, 
			int diasAtraso, Configuracao config) {

	    // Atualiza apenas os campos enviados
	    campos.forEach((campo, valor) -> {
	        switch (campo) {
	            case "dataEntrega" -> livroVenda.setDataEntrega(
	                LocalDate.parse(valor.toString()));
	            
	            // Adicione os campos que precisar...
	        }
	    });

	    livroVendaRepository.save(livroVenda);
	    
	    LocalDate dataLimite = LocalDate.now();
	    switch (config.getPeriodoPenalizacao()) {
	    
			case FORMAT_PERIODO_PENALIZACAO_MENSAL: {
				
				dataLimite = LocalDate.now().minusDays(30);
				break;
			}
			
			case FORMAT_PERIODO_PENALIZACAO_BIMESTRAL: {
				
				dataLimite = LocalDate.now().minusDays(60);
				break;
			}
			
			case FORMAT_PERIODO_PENALIZACAO_TRIMESTRAL: {
				
				dataLimite = LocalDate.now().minusDays(90);
				break;
			}
			
			case FORMAT_PERIODO_PENALIZACAO_SEMESTRAL: {
				
				dataLimite = LocalDate.now().minusDays(180);
				break;
			}
	
			case FORMAT_PERIODO_PENALIZACAO_ANUAL: {
				
				dataLimite = LocalDate.now().minusDays(365);
				break;
			}
	
		}
	    
	    Integer quantPunicoes = penalizacaoRepository.buscarPenalizacaoAtiva(livroVenda.getVenda().getIdAluno(),
	    		livroVenda.getVenda().getCliente() == null ? -1 : livroVenda.getVenda().getCliente().getId(),
	    		livroVenda.getVenda().getIdFuncionario(),
			    dataLimite);
	    
	    if(diasAtraso != 0 && config.getQtasInfracoesPenalizacao() >= quantPunicoes 
	    	&& diasAtraso < 0) {
	    	
	    	Penalizacao penalizacao = new Penalizacao();
	    	penalizacao.setDataInicial(LocalDate.now());
	    	penalizacao.setDataFinal(LocalDate.now().plusDays(config.getQtosDiasPenalizacao()));
	    	penalizacao.setLivroVenda(livroVenda);
	    	
	    	if(config.getValorDiariaMulta() > 0) {
		    	
	    		Pagamento pagamento = new Pagamento();
	    		pagamento.setValor((config.getValorDiariaMulta() + 
	    				((config.getValorDiariaMulta()*config.getJurosAtrasoDiario())/100)*Math.abs(diasAtraso)) 
	    				+ (config.getValorDiariaMulta()*config.getJurosAtrasoMulta()));
	    		pagamento.setDataVencimento(LocalDate.now());
	    		pagamento.setLivroVenda(livroVenda);
		    }
	    } 
	    
	    return livroVenda.getVenda();
	}
	
	public int verificaEstoqueBiblioteca(Integer idLivro) {
		
		Integer qtosLivrosDisponiveis = vendaRepository.buscarQtosLivroBiblioteca(idLivro);
		
		return (qtosLivrosDisponiveis == null) ? 0 : qtosLivrosDisponiveis.intValue();
		
	}
	
	public Integer buscarLivroEmprestado(Integer id, Integer idAluno) {
		 
		Integer qtosLivros = vendaRepository.buscarLivroEmprestado(id, idAluno);
		 
		 return qtosLivros == null ? 0 : qtosLivros;
	} 

	public Integer buscarMesmoLivroEmprestado(Integer id, Integer idAluno) {
		 
		Integer qtosLivros = vendaRepository.buscarMesmoLivroEmprestado(id, idAluno);
		 
		 return qtosLivros == null ? 0 : qtosLivros;
	} 

	public List<LivroVenda> buscarLivroEmprestado(int deslocamento) {
		 
		Pageable pageable = PageRequest.of(deslocamento -1000, deslocamento);
		
	    List<Integer> ids = vendaRepository.buscarIdsEmprestados(pageable);
	    
	    if (ids.isEmpty()) return List.of();
	    
	    List<LivroVenda> livroVendas = livroVendaRepository.buscarVendasComLivrosEAutores(ids);
	    livroVendas.sort(Comparator.comparing(lv -> lv.getVenda().getData())); 
	    
		return livroVendas;
	}
	
	public List<LivroVenda> buscarLivroEmprestado(List<Integer> ids, Integer idAluno) {
		 
		ids = (ids == null || ids.isEmpty()) ? List.of(-1) : ids;
		idAluno = (idAluno == null) ? -1 : idAluno;
		
		List<LivroVenda> livroVendas = livroVendaRepository.buscarLivroEmprestadoPorMatricula(ids, idAluno);
		livroVendas.sort(Comparator.comparing(lv -> lv.getVenda().getData()));
		
		return livroVendas;
	}

	public Venda buscarVenda(Integer idVenda) {
		 
		Venda venda = vendaRepository.findById(idVenda).get();
		 
		return venda;
	}
	
	public List<LivroVenda> findByVenda(Integer idVenda) {
		
		return livroVendaRepository.findByVenda(idVenda);
	}

	public LivroVenda findByLivroVenda(Integer idLivro, Integer idVenda) {
		
		return livroVendaRepository.findByLivroVenda(idLivro, idVenda);
	}

	public LivroVenda findByLivroVenda(Integer id) {
		
		Optional<LivroVenda> optLivroVenda =  livroVendaRepository.findById(id);
		
		return optLivroVenda.get();
	}
}
