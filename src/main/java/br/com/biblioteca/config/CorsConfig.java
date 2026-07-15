package br.com.biblioteca.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

	@Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        config.setAllowCredentials(true);
        /*config.setAllowedOrigins(Arrays.asList(
        	       "http://localhost:5173",
        	       "http://localhost:3000",
        	       "https://seusite.com"
        	   ));*/
        config.addAllowedOrigin("http://localhost:5173"); // URL do frontend
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        
        source.registerCorsConfiguration("/biblioteca/**", config);
        return new CorsFilter(source);
    }
}
