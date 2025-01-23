package org.myteam.server.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        SecurityRequirement securityItem = new SecurityRequirement().addList("bearerAuth");

        return new OpenAPI()
                .openapi("3.0.1")  // OpenAPI 버전 명시
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                .security(List.of(securityItem))
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("PlayHive API")
                .description("PlayHive API Documentation")
                .version("1.0.0");
    }
}