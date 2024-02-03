package com.dodal.meet.configuration;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"local", "dev"})
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI(@Value("${project.version}") String version,
                           @Value("${spring.config.activate.on-profile}") String profile,
                           @Value("${project.yaml-url}") String yamlUrl) {
        final String securitySchemeName = "bearerAuth";
        Info info = new Info()
                .title("도달 프로젝트 API Document " + profile)
                .version(version)
                .description("도달 API 명세서 \n yaml 경로 : " + yamlUrl);
        return new OpenAPI()
                .components(new Components())
                .info(info)
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                ;
    }
}
