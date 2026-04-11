package com.condominium.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI condoGestOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CondoGest API")
                        .description("""
                                API REST para gestão de condomínio residencial.
                                
                                ## Autenticação
                                A maioria dos endpoints requer autenticação via **JWT Bearer Token**.
                                Faça login em `/api/auth/login` para obter o token e clique em **Authorize** para usá-lo.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("CondoGest")
                                .email("***REMOVED***")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Insira o token JWT obtido no login")))
                .tags(List.of(
                        new Tag().name("Autenticação").description("Login e recuperação de senha"),
                        new Tag().name("Moradores").description("Gestão de moradores (SINDICO)"),
                        new Tag().name("Boletos").description("Geração e gestão de boletos"),
                        new Tag().name("Ocorrências").description("Registro e acompanhamento de ocorrências"),
                        new Tag().name("Reservas").description("Reservas de áreas comuns"),
                        new Tag().name("Comunicados").description("Comunicados do condomínio"),
                        new Tag().name("Fornecedores").description("Cadastro de fornecedores"),
                        new Tag().name("Visitantes").description("Controle de acesso de visitantes"),
                        new Tag().name("Áreas Comuns").description("Gestão das áreas comuns"),
                        new Tag().name("Unidades").description("Gestão das unidades/apartamentos"),
                        new Tag().name("Usuários").description("Criação de acesso inicial"),
                        new Tag().name("Log de Acessos").description("Auditoria de acessos ao sistema")
                ));
    }
}
