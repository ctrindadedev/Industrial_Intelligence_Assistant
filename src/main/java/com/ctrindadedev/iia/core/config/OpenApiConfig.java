package com.ctrindadedev.iia.core.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI iiaOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Industrial Intelligence Assistant API")
                        .description("API do assistente de inteligência industrial")
                        .version("v0.1"));
    }
}
