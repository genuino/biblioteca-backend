package br.com.biblioteca.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
public class NotImplementedException extends RuntimeException {

	private static final long serialVersionUID = -1115238347902934071L;

	public NotImplementedException(String msg) {
		super(msg);
	}
}
