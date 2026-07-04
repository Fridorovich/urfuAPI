package org.urfu.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация Swagger/OpenAPI для документирования API.
 * Настраивает заголовок, описание, контактную информацию и схему безопасности.
 */
@Configuration
@SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic",
        description = "Basic Authentication. Используйте логин: admin, пароль: admin123"
)
public class SwaggerConfig {

    /**
     * Настройка OpenAPI документации.
     * Добавляет информацию о проекте, версии, контактах и настройки безопасности.
     *
     * @return настроенный объект OpenAPI
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Education Service API")
                        .version("1.0.0")
                        .description("""
                                REST API для управления образовательными программами УрФУ.
                                
                                ## Возможности API:
                                * CRUD операции для образовательных программ
                                * CRUD операции для институтов
                                * CRUD операции для ответственных лиц
                                * CRUD операции для модулей
                                * Получение программ с вложенными модулями
                                * Сортировка программ по различным полям
                                * Управление связями программ и модулей
                                
                                ## Аутентификация:
                                Используется Basic Auth.
                                По умолчанию: admin / admin123
                                """)
                        .contact(new Contact()
                                .name("Support Team")
                                .email("support@urfu.ru")
                                .url("https://urfu.ru"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"));
    }
}