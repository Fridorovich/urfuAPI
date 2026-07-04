package org.urfu.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Конфигурация безопасности приложения.
 * Настраивает Basic аутентификацию с использованием InMemoryUserDetailsManager.
 * Swagger UI доступен без авторизации для удобства.
 * Все остальные эндпоинты требуют аутентификации.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${spring.security.user.name:admin}")
    private String username;

    @Value("${spring.security.user.password:admin123}")
    private String password;

    /**
     * Настройка цепочки фильтров безопасности.
     * Отключает CSRF, устанавливает stateless сессии и настраивает Basic Auth.
     *
     * @param http объект HttpSecurity для настройки
     * @return настроенный SecurityFilterChain
     * @throws Exception если произошла ошибка конфигурации
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> {});

        return http.build();
    }

    /**
     * Создает сервис для управления пользователями в памяти.
     * Создает одного пользователя с указанными в конфигурации логином и паролем.
     *
     * @return UserDetailsService с одним пользователем
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username(username)
                .password(passwordEncoder().encode(password))
                .roles("USER", "ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user);
    }

    /**
     * Создает кодировщик паролей с использованием BCrypt.
     * Используется для безопасного хранения паролей в памяти.
     *
     * @return PasswordEncoder для кодирования паролей
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}