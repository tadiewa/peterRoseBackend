package com.peterrose.peterrose.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI Configuration
 * Generates interactive API documentation
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI peterRoseAPI() {
        // Security scheme for JWT
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("bearerAuth");

        return new OpenAPI()
                .info(new Info()
                        .title("Peter Rose Florist API")
                        .description("REST API for Peter Rose e-commerce flower shop")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Peter Rose")
                                .email("Peterroseenquiries@gmail.com")
                                .url("https://peterrose.co.za"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://peterrose.co.za/terms")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:9039")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.peterrose.co.za")
                                .description("Production Server")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", securityScheme))
                .addSecurityItem(securityRequirement);
    }
}