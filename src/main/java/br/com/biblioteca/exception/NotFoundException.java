package br.com.biblioteca.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//exception/LivroNotFoundException.java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
 
	private static final long serialVersionUID = -1299993271674140401L;

	public NotFoundException(String msg) {
     super(msg);
 }
}
