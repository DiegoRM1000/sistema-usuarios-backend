package com.usersystem.sistemausuariosbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Ya lo tienes en Application
import org.springframework.security.crypto.password.PasswordEncoder; // Ya lo tienes en Application
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration // Indica que esta clase contiene configuraciones de Spring
@EnableWebSecurity // Habilita la seguridad web de Spring
@EnableMethodSecurity // Habilita la seguridad a nivel de métodos (ej. @PreAuthorize)
public class WebSecurityConfig {

    // Aquí inyectaremos los componentes JWT que crearemos después

    // Este bean define la cadena de filtros de seguridad HTTP
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para APIs REST (ya que usamos JWT)
                .cors(cors -> {}) // Permitir configuración de CORS (Cross-Origin Resource Sharing)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/**").permitAll() // Permitir acceso sin autenticación a endpoints de autenticación
                        .anyRequest().authenticated() // Cualquier otra petición requiere autenticación
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // No usar sesiones HTTP (JWT es stateless)
                );
        // Aquí se añadirán los filtros JWT que crearemos más tarde
        return http.build();
    }

    // Este bean expone el AuthenticationManager para que podamos usarlo en nuestro controlador de autenticación
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}