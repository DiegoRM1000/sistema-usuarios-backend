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
import com.usersystem.sistemausuariosbackend.security.JwtAuthEntryPoint; // Importa esto
import com.usersystem.sistemausuariosbackend.security.JwtAuthFilter;     // Importa esto
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Importa esto



@Configuration // Indica que esta clase contiene configuraciones de Spring
@EnableWebSecurity // Habilita la seguridad web de Spring
@EnableMethodSecurity // Habilita la seguridad a nivel de métodos (ej. @PreAuthorize)
public class WebSecurityConfig {

    // Aquí inyectaremos los componentes JWT que crearemos después
    private final JwtAuthEntryPoint unauthorizedHandler; // Inyecta el punto de entrada
    private final JwtAuthFilter jwtAuthFilter; // Inyecta el filtro JWT

    // Constructor para inyección de dependencias
    public WebSecurityConfig(JwtAuthEntryPoint unauthorizedHandler, JwtAuthFilter jwtAuthFilter) {
        this.unauthorizedHandler = unauthorizedHandler;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    // Este bean define la cadena de filtros de seguridad HTTP
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para APIs REST
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler)) // Configurar el manejador de errores de autenticación
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // No usar sesiones HTTP
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/**").permitAll() // Permitir acceso sin autenticación a /api/auth/**
                        .anyRequest().authenticated() // Cualquier otra petición requiere autenticación
                        // Cuando estés listo para que solo el admin registre
                        //.requestMatchers("/api/auth/login").permitAll()
                        //.requestMatchers("/api/auth/register").hasRole("ADMIN")
                        //.anyRequest().authenticated()
                );

        // Añadir el filtro JWT antes del filtro de autenticación de usuario/contraseña de Spring Security
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

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