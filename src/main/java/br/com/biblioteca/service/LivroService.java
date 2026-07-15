package br.com.biblioteca.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.biblioteca.domain.Livro;
import br.com.biblioteca.exception.ConflictException;
import br.com.biblioteca.exception.NotFoundException;
import br.com.biblioteca.repository.LivroRepository;
import br.com.biblioteca.util.Constantes;
import br.com.biblioteca.util.Util;

@Service
public class LivroService {

	private static final Logger logger = LogManager.getLogger(LivroService.class);
	
	@Autowired
	LivroRepository livroRepository;

	@Autowired
	ReservaService reservaService;

	@Autowired
	PenalizacaoService penalizacaoService;

	@Autowired
	MultaService multaService;
	
    @Value("${upload.path}")
    private String uploadPath;
    
	public Livro salvarLivro(Livro livro) {
		
		try {
			
			livro.setId(livroRepository.findMaxId() + 1);
			
			logger.info("Id do livro: {}", livro.getId());
			System.out.println("Id do livro: " + livro.getId());
			
			String caminhoImagem = Util.salvarImagem(livro.getImagem(), 
					uploadPath, Constantes.LIVRO.concat(livro.getId().toString()));
			livro.setImagem(caminhoImagem);
			
			Livro livroRet = livroRepository.save(livro);
			
			livroRepository.flush();
		
			return livroRet;
			
		} catch(IOException e) {
			
			logger.error(e.getMessage());
			return null;
		}
	}
	
	public List<String> buscarLivroAutor(String titulo, List<String> autores) {
		
		 List<String> listaMaiusculas = autores.stream()
		            .map(s -> s.toUpperCase())
		            .collect(Collectors.toList());
		 
		List<String> livroAutores = livroRepository.findByTitulo(titulo.toUpperCase(), listaMaiusculas);
		
		return livroAutores;
	}
	
	public List<Livro> buscarLivro(String titulo) {
		
		System.out.println("=============Buscar livro================");
	    System.out.println("Parâmetro livro recebido: [" + titulo + "]");
	    System.out.println("Tamanho: " + titulo.length());
	    System.out.println("=========================================");
		List<Livro> livros = livroRepository.buscarTitulo("%".concat(titulo).concat("%"));
		
		return livros;
	}

	public Livro buscarLivroPorId(int idLivro) {
		
		return livroRepository.findById(idLivro)
			.orElseThrow(() -> new NotFoundException("Livro não encontrado: ".
					concat(Integer.toString(idLivro))));
		
	}

	public String situacaoLivro(int idLivro, String livro,
				Integer idAluno, String nome, String dataInicial, String dataFinal,
				int tipoPesquisa) throws Exception {
        
		StringBuilder erros = new StringBuilder();
		StringBuilder msg = new StringBuilder();
		
		if(Constantes.PESQUISAR_RESERVA_ALUNO == tipoPesquisa) {
			
			String quantasReservas = reservaService.restricoesReservarLivro(idLivro, idAluno, 
					 -1);
			
			char[] quantasReservasArray = quantasReservas.toCharArray();
			int quantasReservasInt = 0;
			int quantasReservasMesmoLivro = 0;
			
			for(int i = 0; i < quantasReservasArray.length;i++) {
				
				if(i == 0 && (quantasReservasArray[i] == '-' || quantasReservasArray[i] == '0')) {
					if(quantasReservasArray[i] == '-') {
						
						erros.append("Reservas esgotadas para o livro "
							.concat(livro)
							.concat(". Cancele uma ou mais para realizar novas reservas.\n"));
						
					} else {
						
						quantasReservasInt = Integer.parseInt(Character.toString(quantasReservasArray[i]));

						if(quantasReservasInt > 0 && quantasReservasInt < 1) {
							
							msg.append("Disponível ".concat(Integer.toString(quantasReservasInt))
									.concat(" reserva(s) para o livro ").concat(livro).concat("\n"));
						}
					}					
				} else {
					
					if(quantasReservasArray[i] == '-') {
						
						erros.append("Reserva esgotada para o mesmo livro: ")
							.append(livro).append("\n");
						
					} else {
						
						quantasReservasMesmoLivro = Integer.parseInt(Character.toString(quantasReservasArray[i]));
						
						if(quantasReservasMesmoLivro > 0 
								&& quantasReservasMesmoLivro < 1) {
							
							msg.append("Disponível ".concat(Integer.toString(quantasReservasMesmoLivro))
									.concat(" reserva(s) para o livro ").concat(livro).concat("\n"));
						}
					}
					
				}
			}
			
			int retorno = multaService.clienteMultado(idAluno, 
					 -1);  
			
			if(retorno < 0) {
				
				erros.append("O aluno tem ".concat(Integer.toString(retorno).replace("-", ""))
						.concat(" multa(s). Impossibilitando de realizar empréstimo."));
				
			} else {
				
				LocalDate dataFinalLD = penalizacaoService.clientePenalizado(idAluno, -1);
				
				if(dataFinalLD != null) {

					erros.append("O aluno ")
						.append(nome.concat(" tem ").concat(Integer.toString(retorno).replace("-", ""))
							.concat(" multa(s). Impossibilitando de realizar empréstimo."));
					
				} 
			}
		}
		
		if(Constantes.PESQUISAR_RESERVA_lIVRO == tipoPesquisa) {

			String datas = reservaService.livroReservado(idLivro, idAluno);
			
			if(!datas.trim().isEmpty()) {
				
				erros.append("Livro ".concat(nome).concat(" reservado nas datas "
					.concat(datas.substring(0, datas.length() - 1))
					.concat(" Impossibilitando de realizar empréstimo.")));
			}
		}
		
		if (!erros.isEmpty()) {
	        throw new ConflictException(erros.toString());
	    }
		
		return msg.toString();
	}
}
