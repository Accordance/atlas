package com.accordance.atlas;

import com.fasterxml.classmate.TypeResolver;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;

import static springfox.documentation.schema.AlternateTypeRules.*;

@PropertySource("classpath:swagger.properties")
@Configuration
public class SwaggerConfiguration {
    @Value("${springfox.documentation.swagger.v2.path}")
    private String swagger2Url;
    @Value("${springfox.documentation.swagger.v1.path}")
    private String swagger1Url;

    @Autowired
    private TypeResolver typeResolver;

    @Bean
    public Docket swaggerApi() {
        Docket result = new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
        return result
                .genericModelSubstitutes(ResponseEntity.class)
                .alternateTypeRules(deferredResultRule())
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, Collections.singletonList(new ResponseMessageBuilder().code(500).message("500 message").responseModel(new ModelRef("Error")).build()))
                .securityContexts(Collections.singletonList(securityContext()));
    }

    private AlternateTypeRule deferredResultRule() {
        return newRule(typeResolver.resolve(DeferredResult.class, typeResolver.resolve(ResponseEntity.class, WildcardType.class)), typeResolver.resolve(WildcardType.class));
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).forPaths(PathSelectors.regex("/anyPath.*")).build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Collections.singletonList(new SecurityReference("mykey", authorizationScopes));
    }

    @Bean
    public UiConfiguration uiConfig() {
        return new UiConfiguration("validatorUrl");
    }
}
