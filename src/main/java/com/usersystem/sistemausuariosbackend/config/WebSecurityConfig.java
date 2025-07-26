package com.usersystem.sistemausuariosbackend.config;


import com.usersystem.sistemausuariosbackend.security.CustomAccessDeniedHandler; // <-- ¡Añade este import!
import com.usersystem.sistemausuariosbackend.security.JwtAuthFilter; // Ya debería estar
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Importa esto
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // <-- Añade esta anotación


@Configuration // Indica que esta clase contiene configuraciones de Spring
@EnableWebSecurity // Habilita la seguridad web de Spring
@EnableMethodSecurity // Habilita la seguridad a nivel de métodos (ej. @PreAuthorize)

public class WebSecurityConfig {

    // Aquí inyectaremos los componentes JWT que crearemos después
    private final JwtAuthEntryPoint unauthorizedHandler; // Inyecta el punto de entrada
    private final JwtAuthFilter jwtAuthFilter; // Inyecta el filtro JWT
    private final CustomAccessDeniedHandler customAccessDeniedHandler; // <-- Inyecta este

    // Constructor para inyección de dependencias
    public WebSecurityConfig(JwtAuthEntryPoint unauthorizedHandler, JwtAuthFilter jwtAuthFilter,CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.unauthorizedHandler = unauthorizedHandler;
        this.jwtAuthFilter = jwtAuthFilter;
        this.customAccessDeniedHandler = customAccessDeniedHandler; // <-- Asígnalo
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Este bean expone el AuthenticationManager para que podamos usarlo en nuestro controlador de autenticación
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Este bean define la cadena de filtros de seguridad HTTP
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler) // Para 401 (no autenticado)
                        .accessDeniedHandler(customAccessDeniedHandler) // <-- ¡Añade esto para 403 (autenticado pero sin permiso)!
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/register").permitAll()
                        .requestMatchers("/api/users/me").authenticated()
                        .requestMatchers("/api/users/**").hasRole("ADMIN") // <-- Vuelve a tener esta línea
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }




}