package br.com.biblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.biblioteca.domain.Configuracao;

@Repository
public interface ConfiguracaoRepository extends JpaRepository<Configuracao, Integer> {

}
