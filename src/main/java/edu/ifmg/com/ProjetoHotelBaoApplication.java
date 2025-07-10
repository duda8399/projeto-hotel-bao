package edu.ifmg.com;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(
		title = "Hotel Bão",
		version = "1.0",
		description = "Documentação da API do Hotel Bão"
))
@SpringBootApplication
public class ProjetoHotelBaoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjetoHotelBaoApplication.class, args);
	}

}
