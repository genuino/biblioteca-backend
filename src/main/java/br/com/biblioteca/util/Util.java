package br.com.biblioteca.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import br.com.biblioteca.domain.Autor;
import br.com.biblioteca.domain.AutorDTO;
import br.com.biblioteca.domain.Categoria;
import br.com.biblioteca.domain.CategoriaDTO;
import br.com.biblioteca.domain.Cliente;
import br.com.biblioteca.domain.ClienteDTO;
import br.com.biblioteca.domain.Livro;
import br.com.biblioteca.domain.LivroDTO;
import br.com.biblioteca.domain.LivroVenda;
import br.com.biblioteca.domain.Reserva;
import br.com.biblioteca.domain.ReservaDTO;
import br.com.biblioteca.domain.Venda;
import br.com.biblioteca.domain.VendaDTO;
import br.com.biblioteca.service.CategoriaService;
import lombok.Synchronized;

public class Util {

	private static final Logger logger = LogManager.getLogger(Util.class);
	
    private static CategoriaService categoriaService;
    
    @Autowired
    public Util(CategoriaService categoriaService) {
        Util.categoriaService = categoriaService; // Alimenta a propriedade estática
    }
	
    public Util() {}
    
	public void criarQRCode(String uploadPath, String texto, String nomeArquivo, String formatoQrCodeGerado) {
		
		try {
			
			Path directory = Paths.get(uploadPath).toAbsolutePath().normalize();
            Files.createDirectories(directory);
			
            Path targetLocation = directory.resolve(nomeArquivo.
	        		concat(".").concat(formatoQrCodeGerado));
            
			File myFile = targetLocation.toFile();
			Hashtable<EncodeHintType, ErrorCorrectionLevel>
			hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
			hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			
			try {
				BitMatrix byteMatrix = qrCodeWriter.encode(texto,BarcodeFormat.QR_CODE, 100, 100, hintMap);
				
				int CrunchifyWidth = byteMatrix.getWidth();
			    BufferedImage image = new BufferedImage(CrunchifyWidth, CrunchifyWidth,
			            BufferedImage.TYPE_INT_RGB);
			    image.createGraphics();
		
			    Graphics2D graphics = (Graphics2D) image.getGraphics();
			    graphics.setColor(Color.WHITE);
			    graphics.fillRect(0, 0, CrunchifyWidth, CrunchifyWidth);
			    graphics.setColor(Color.BLACK);
		
			    for (int i = 0; i < CrunchifyWidth; i++) {
			        for (int j = 0; j < CrunchifyWidth; j++) {
			            if (byteMatrix.get(i, j)) {
			                graphics.fillRect(i, j, 1, 1);
			            }
			        }
			    }
		    	
		    	ImageIO.write(image, formatoQrCodeGerado, myFile);
			
			} catch (WriterException e) {
			    e.printStackTrace();
			}    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	public static String formatLocalDatetoString(LocalDate data) {
	
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String dataFormatada = data.format(formatter);
		
		return dataFormatada;
	}

	public static LocalDate formatStringtoLocalDate(String data) {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate date = LocalDate.parse(data, formatter);
		
		return date;
	}
	
	public static String salvarImagem(String base64, String uploadPath, String nome) throws IOException {

	    // Extrai o tipo da imagem do prefixo "data:image/jpeg;base64,"
	    String tipoImagem = base64.substring(
	        base64.indexOf("/") + 1,  // pega depois do "/"
	        base64.indexOf(";")       // até o ";"
	    );
	    // tipoImagem = "jpeg" ou "png" ou "gif"

	    // Trata jpeg → jpg
	    String extensao = tipoImagem.equals("jpeg") ? "jpg" : tipoImagem;

	    // Remove o prefixo
	    String base64Puro = base64.replaceAll("data:image/.*;base64,", "");

	    // Converte para byte[]
	    byte[] imagemBytes = Base64.getDecoder().decode(base64Puro);

	    // Gera nome único com a extensão correta
	    String nomeArquivo = nome + "." + extensao;
	    // → "550e8400-e29b-41d4-a716.jpg"
	    // → "550e8400-e29b-41d4-a716.png"
	    // → "550e8400-e29b-41d4-a716.gif"

	    // Salva no HD
	    Path caminho = Paths.get(uploadPath + nomeArquivo);
	    Files.write(caminho, imagemBytes);

	    return nomeArquivo;
	}
	
	public  static LivroDTO formatarLivroParaLivroDTO(Livro livro) {
		
		LivroDTO livroDTO = new LivroDTO();
		
		livroDTO.setCopia(livro.getCopia());
		livroDTO.setCategoria(formatarCategoriaParaCategoriaDTO(livro.getCategoria()));
		livroDTO.setDescricao(livro.getDescricao());
		livroDTO.setEdicao(livro.getEdicao());
		livroDTO.setEditora(livro.getEditora());
		livroDTO.setId(livro.getId());
		livroDTO.setImagem(livro.getImagem());
		livroDTO.setTitulo(livro.getTitulo());
		
		List<AutorDTO> autoresDTO = new ArrayList<>();
		for(Autor autor: livro.getAutores()) {
			
			autoresDTO.add(formatarAutorParaAutorDTO(autor));
		}
		livroDTO.setAutores(autoresDTO);
		
		return livroDTO;
	}
	
	// DTO → Entidade
    public static Livro toEntity(LivroDTO dto) {
        if (dto == null) return null;

        Livro livro = new Livro();
        livro.setId(dto.getId());
        livro.setTitulo(dto.getTitulo());
        livro.setEdicao(dto.getEdicao());
        livro.setEditora(dto.getEditora());
        livro.setDescricao(dto.getDescricao());
        livro.setImagem(dto.getImagem());
        livro.setCopia(dto.getCopia());

        if (dto.getCategoria() != null) {
            livro.setCategoria(categoriaToEntity(dto.getCategoria()));
        }

        if (dto.getAutores() != null) {
            livro.setAutores(
                dto.getAutores().stream()
                    .map(Util::autorToEntity)
                    .collect(Collectors.toSet())
            );
        }

        return livro;
    }
	
	public  static AutorDTO formatarAutorParaAutorDTO(Autor autor) {
		
		AutorDTO autorDTO = new AutorDTO();
		
		autorDTO.setId(autor.getId().longValue());
		autorDTO.setNome(autor.getNome());
		
		return autorDTO;
	}

	private static Autor autorToEntity(AutorDTO dto) {
        if (dto == null) return null;

        Autor autor = new Autor();
        autor.setId(dto.getId() != null ? dto.getId().intValue() : null); // Long → Integer
        autor.setNome(dto.getNome());        // AutorDTO.autor → Autor.nome
        return autor;
    }
	
	@Synchronized
	public  static CategoriaDTO formatarCategoriaParaCategoriaDTO(Categoria categoria) {
		
		CategoriaDTO categoriaDTO = new CategoriaDTO();
		
		categoriaDTO.setId(categoria.getId());
		categoriaDTO.setCategoria(categoria.getCategoria());	
		
		categoriaService.setCaminhoTodaCategoria(categoria.getCategoria());
		categoriaService.buscarCategoriasTodosPai(categoria.getIdCategoriaPai());
		categoriaDTO.setCaminhoCategoria(categoriaService.getCaminhoTodaCategoria());
		//categoriaService.setCaminhoTodaCategoria(null);

		logger.info("Caminho da categoria: " + categoriaDTO.getCaminhoCategoria());
		
		return categoriaDTO;
	}

    private static Categoria categoriaToEntity(CategoriaDTO dto) {
        if (dto == null) return null;

        Categoria categoria = new Categoria();
        categoria.setId(dto.getId());
        categoria.setCategoria(dto.getCategoria()); // CategoriaDTO.name → Categoria.categoria
        return categoria;
    }
    
    // DTO → Entidade
    public static Reserva toEntity(ReservaDTO dto) {
      if (dto == null) return null;

        Reserva reserva = new Reserva();
        reserva.setId(dto.getId());
        reserva.setDataInicial(
            dto.getDataInicial() != null
                ? formatStringtoLocalDate(dto.getDataInicial())
                : null
        );
        reserva.setIdAluno(dto.getIdAluno());
        reserva.setCopia(dto.getCopia());

        // Relacionamentos — precisam ser resolvidos via repositório no service
        // reserva.setCliente(...)
        // reserva.setLivros(...)

        return reserva;
    }

    // Entidade → DTO
    public static ReservaDTO toDTO(Reserva reserva) {
    	
    	if (reserva == null) return null;

        	ReservaDTO dto = new ReservaDTO();
	        dto.setId(reserva.getId());
	        dto.setDataInicial(
	            reserva.getDataInicial() != null
	                ? formatLocalDatetoString(reserva.getDataInicial())
	                : null
	        );
	        dto.setIdAluno(reserva.getIdAluno());
	        dto.setCopia(reserva.getCopia());

	        /*if (livros != null) {
	            dto.setLivros(
	                livros.stream()
	                    .map(Util::formatarLivroParaLivroDTO)   // ajuste conforme seu mapper de Livro
	                    .collect(Collectors.toList())
	            );
	        }*/

	        if (reserva.getCliente() != null) {
	            dto.setClienteDTO(toDTO(reserva.getCliente())); // ajuste conforme seu mapper de Cliente
	        }

	        return dto;
	  }
	
      // Entidade → DTO
      public static ClienteDTO toDTO(Cliente cliente) {
            if (cliente == null) return null;

            ClienteDTO dto = new ClienteDTO();
            dto.setId(cliente.getId());
            dto.setNome(cliente.getNome());
            dto.setTelefone(cliente.getTelefone());
            dto.setEndereco(cliente.getEndereco());
            dto.setNumero(cliente.getNumero());
            dto.setComplemento(cliente.getComplemento());
            dto.setBairro(cliente.getBairro());
            dto.setCep(cliente.getCep());
            dto.setIdCidade(cliente.getIdCidade());

            return dto;
        }

        // DTO → Entidade
        public static Cliente toEntity(ClienteDTO dto) {
            if (dto == null) return null;

            Cliente cliente = new Cliente();
            cliente.setId(dto.getId());
            cliente.setNome(dto.getNome());
            cliente.setTelefone(dto.getTelefone());
            cliente.setEndereco(dto.getEndereco());
            cliente.setNumero(dto.getNumero());
            cliente.setComplemento(dto.getComplemento());
            cliente.setBairro(dto.getBairro());
            cliente.setCep(dto.getCep());
            cliente.setIdCidade(dto.getIdCidade());

            return cliente;
        }
        
        public static VendaDTO toDTO(Venda venda, LivroVenda livroVenda, List<LivroDTO> livros) {
            if (venda == null) {
                return null;
            }

            VendaDTO dto = new VendaDTO();
            dto.setId(venda.getId());
            dto.setValor(venda.getValor());
            dto.setData(venda.getData());
            dto.setCliente(venda.getCliente());
            dto.setCopia(venda.getCopia());
            dto.setEmprestimo(venda.getEmprestimo());
            dto.setIdAluno(venda.getIdAluno());
            dto.setIdFuncionario(venda.getIdFuncionario());

            dto.setLivros(livros.stream()
            	    .collect(Collectors.toSet()));
            dto.setDataEntrega(livroVenda.getDataEntrega());
            
            return dto;
        }

        public static Venda toEntity(VendaDTO dto) {
            if (dto == null) {
                return null;
            }

            Venda venda = new Venda();
            venda.setId(dto.getId());
            venda.setValor(dto.getValor());
            venda.setData(dto.getData());
            venda.setCliente(dto.getCliente());
            venda.setCopia(dto.getCopia());
            venda.setEmprestimo(dto.getEmprestimo());
            venda.setIdAluno(dto.getIdAluno());
            venda.setIdFuncionario(dto.getIdFuncionario());

            // dataEntrega e livros do DTO são ignorados aqui,
            // pois não têm campo correspondente na entidade Venda
            return venda;
        }
}
