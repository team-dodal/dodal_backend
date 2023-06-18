package com.dodal.meet.configuration;

import com.dodal.meet.controller.response.Response;
import com.dodal.meet.exception.ErrorCode;
import com.fasterxml.classmate.TypeResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.HttpAuthenticationScheme;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;


@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private static final String BEARER = "Bearer ";

    @Bean
    public Docket api(TypeResolver typeResolver) {
        return new Docket(DocumentationType.OAS_30)
                .additionalModels(
                        typeResolver.resolve(Response.class)
                )
                .useDefaultResponseMessages(true)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dodal.meet.controller"))
                .paths(PathSelectors.any())
                .build()
                .securityContexts(List.of(securityContext()))
                .securitySchemes(List.of(httpAuthenticationScheme()))
                ;
    }

    private springfox.documentation.spi.service.contexts.SecurityContext securityContext() {
        return  springfox.documentation.spi.service.contexts
                .SecurityContext.builder()
                .securityReferences(defaultAuth())
                .operationSelector(operationContext -> true)
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = new AuthorizationScope("global", "accessEverything");
        return List.of(new SecurityReference(BEARER, authorizationScopes));
    }

    private HttpAuthenticationScheme httpAuthenticationScheme() {
        return HttpAuthenticationScheme.JWT_BEARER_BUILDER.name(BEARER).build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Dodal Rest API Documentation")
                .description("도달 API 문서")
                .version("0.1")
                .build();
    }
}
