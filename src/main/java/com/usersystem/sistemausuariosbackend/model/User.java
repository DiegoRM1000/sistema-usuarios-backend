package com.usersystem.sistemausuariosbackend.model;

import jakarta.persistence.*; // Importa clases de JPA
import lombok.Data; // Importa anotación @Data de Lombok
import lombok.NoArgsConstructor; // Importa anotación @NoArgsConstructor de Lombok
import lombok.AllArgsConstructor; // Importa anotación @AllArgsConstructor de Lombok
import java.time.LocalDateTime; // Para manejar fechas y horas
import java.util.HashSet; // Para el conjunto de roles
import java.util.Set; // Interfaz para el conjunto de roles

@Entity // Indica que esta clase es una entidad JPA y se mapeará a una tabla de DB
@Table(name = "users") // Especifica el nombre de la tabla en la DB
@Data // Genera automáticamente getters, setters, toString, equals y hashCode con Lombok
@NoArgsConstructor // Genera un constructor sin argumentos (requerido por JPA)
@AllArgsConstructor // Genera un constructor con todos los argumentos

public class User {
    @Id // Indica que este campo es la clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Estrategia para autoincrementar el ID
    private Long id;

    @Column(unique = true, nullable = false, length = 50) // Mapea a una columna, única, no nula, con longitud
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password; // ¡IMPORTANTE! Esta contraseña DEBE estar encriptada en DB.

    @Column(name = "first_name", length = 50) // Mapea a una columna con nombre diferente al campo Java
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(nullable = false)
    private boolean enabled = true; // Indica si el usuario está habilitado (por defecto true)

    @Column(name = "two_factor_secret") // Clave secreta para autenticación de dos factores
    private String twoFactorSecret;

    @Column(name = "two_factor_enabled", nullable = false) // Estado de 2FA para el usuario (por defecto false)
    private boolean twoFactorEnabled = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt; // Fecha y hora de creación del usuario

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // Última fecha y hora de actualización del usuario

    @ManyToMany(fetch = FetchType.EAGER) // Relación Muchos a Muchos con la entidad Role
    // fetch = FetchType.EAGER significa que los roles se cargarán junto con el usuario
    @JoinTable( // Configura la tabla intermedia (join table)
            name = "user_roles", // Nombre de la tabla de unión en la DB
            joinColumns = @JoinColumn(name = "user_id"), // Columna que mapea a la clave primaria de User
            inverseJoinColumns = @JoinColumn(name = "role_id") // Columna que mapea a la clave primaria de Role
    )
    private Set<Role> roles = new HashSet<>(); // Un Set para almacenar los roles del usuario

    // Métodos de ciclo de vida de JPA para gestionar timestamps automáticamente
    @PrePersist // Se ejecuta antes de guardar una nueva entidad
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate // Se ejecuta antes de actualizar una entidad existente
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}