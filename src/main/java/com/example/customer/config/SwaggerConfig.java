package com.example.customer.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Customer API")
                        .description("Spring Boot REST API for Customer Management")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Harinath")
                                .email("harinath.ediga23@gmail.com")
                                .url("https://www.linkedin.com/in/harinath-ediga/"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org"))
                )
                .externalDocs(new ExternalDocumentation()
                        .description("Project Wiki Documentation")
                        .url("https://example.com/wiki"));
    }

    @Bean
    public GroupedOpenApi customerApi() {
        return GroupedOpenApi.builder()
                .group("customers")
                .pathsToMatch("/customers/**")
                .build();
    }
}
