package com.eatclub.challenge.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for API documentation.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("EatClub Restaurant Deals API")
                        .version("1.0")
                        .description("REST API for querying restaurant deals and calculating peak availability times. Supports both 12-hour (3:00pm) and 24-hour (15:00) time formats.")
                        .contact(new Contact()
                                .name("EatClub Team")
                                .email("support@eatclub.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local development server")
                ));
    }
}
