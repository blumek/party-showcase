package dev.blumek.party.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
class OpenApiConfiguration {

    @Bean
    OpenAPI partyShowcaseOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Party Showcase API")
                .description("People and organizations, the roles they play, "
                        + "their addresses, capabilities and the relationships between them.")
                .version("v1"));
    }
}
