package com.ankit.patientclinicals.clinicalsapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for the Clinical Data API
 * Configures OpenAPI 3.0 documentation with proper handling of validation annotations
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Clinical Data API")
                        .version("1.0.0")
                        .description("API for managing patient clinical data with comprehensive validation")
                        .contact(new Contact()
                                .name("Clinical API Support")
                                .url("http://localhost:8080/patientservices")));
    }
}
