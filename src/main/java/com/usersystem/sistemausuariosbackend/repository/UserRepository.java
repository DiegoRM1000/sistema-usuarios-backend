package com.usersystem.sistemausuariosbackend.repository;

import com.usersystem.sistemausuariosbackend.model.User; // Importa tu modelo User
import org.springframework.data.jpa.repository.JpaRepository; // Importa JpaRepository
import org.springframework.stereotype.Repository; // Indica que es un componente de repositorio

import java.util.Optional; // Para manejar resultados que pueden o no existir

@Repository // Le dice a Spring que esta interfaz es un componente de repositorio
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring Data JPA generará automáticamente la implementación de este metodo
    Optional<User> findByUsername(String username); // Buscar usuario por nombre de usuario

    // Buscar usuario por email
    Optional<User> findByEmail(String email);
}