package br.com.biblioteca.config;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "br.com.biblioteca.domain")
@EnableJpaRepositories(basePackages = "br.com.biblioteca.repository")
public class JpaConfig {

}
