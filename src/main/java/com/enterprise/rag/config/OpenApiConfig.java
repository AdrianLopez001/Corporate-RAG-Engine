package com.enterprise.rag.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Corporate RAG Engine API")
                        .description("Enterprise Retrieval-Augmented Generation backend powered by Spring AI and pgvector.")
                        .version("1.0.0"));
    }
}
