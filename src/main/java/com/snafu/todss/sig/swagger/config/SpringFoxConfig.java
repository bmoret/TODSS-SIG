package com.snafu.todss.sig.swagger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableSwagger2
@EnableOpenApi
public class SpringFoxConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .select()
                .paths(PathSelectors.ant("/api/**"))
                .apis(RequestHandlerSelectors.basePackage("com.snafu.todss.sig"))
                .build()
                .apiInfo(apiDetails());
    }

    private ApiInfo apiDetails() {
        return new ApiInfo(
                "Kennis sessie application",
                "Application voor CIMSOLUTIONS om kennis sessies in te plannen, aan te melden en inzicht in te krijgen",
                "0.1",
                "To Be Added TOS link", //todo
                new Contact(
                        "Jona Leeflang",
                        "https://github.com/ChromaChroma",
                        "jona.beer@gmail.com"
                ),
                "To Be Added License type",  //todo
                "To Be added License Url", //todo
                Collections.emptyList()
        );
    }

}
