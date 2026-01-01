package com.chanshop.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Server")
                ));
    }

    private Info apiInfo() {
        return new Info()
                .title("ChanShop API")
                .description("ChanShop 쇼핑몰 API 문서")
                .version("1.0.0")
                .contact(new Contact()
                        .name("ChanShop Team")
                        .email("contact@chanshop.com"));
    }
}
