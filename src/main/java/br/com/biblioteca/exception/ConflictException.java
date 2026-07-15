package br.com.biblioteca.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//exception/ConflictException.java — para qualquer 409
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {
 private static final long serialVersionUID = 4403463889606255886L;

 public ConflictException(String mensagem) {
     super(mensagem);
 }
}
