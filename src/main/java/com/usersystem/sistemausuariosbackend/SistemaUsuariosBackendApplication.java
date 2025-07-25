package com.usersystem.sistemausuariosbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.usersystem.sistemausuariosbackend.model.Role; // Importa Role
import com.usersystem.sistemausuariosbackend.model.User; // Importa User
import com.usersystem.sistemausuariosbackend.repository.RoleRepository; // Importa RoleRepository
import com.usersystem.sistemausuariosbackend.repository.UserRepository; // Importa UserRepository
import org.springframework.boot.CommandLineRunner; // Importa CommandLineRunner
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder; // Importa PasswordEncoder

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


@SpringBootApplication
public class SistemaUsuariosBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaUsuariosBackendApplication.class, args);
	}


	@Bean
	public CommandLineRunner run(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			// 1. Crear el rol ADMIN si no existe
			if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
				Role adminRole = new Role();
				adminRole.setName("ROLE_ADMIN");
				roleRepository.save(adminRole);
				System.out.println("Rol ROLE_ADMIN creado.");
			}

			// 2. Crear el rol EMPLOYEE (o USER) si no existe
			if (roleRepository.findByName("ROLE_EMPLOYEE").isEmpty()) {
				Role employeeRole = new Role();
				employeeRole.setName("ROLE_EMPLOYEE");
				roleRepository.save(employeeRole);
				System.out.println("Rol ROLE_EMPLOYEE creado.");
			}

			// 3. Crear un usuario administrador si no existe
			if (userRepository.findByUsername("admin").isEmpty()) {
				User adminUser = new User();
				adminUser.setUsername("admin");
				adminUser.setEmail("admin@example.com");
				adminUser.setPassword(passwordEncoder.encode("adminpassword")); // Contraseña encriptada
				adminUser.setFirstName("Super");
				adminUser.setLastName("Admin");
				adminUser.setEnabled(true);
				adminUser.setCreatedAt(LocalDateTime.now());
				adminUser.setUpdatedAt(LocalDateTime.now());

				// Asignar el rol ADMIN al usuario
				Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found!"));
				Set<Role> roles = new HashSet<>();
				roles.add(adminRole);
				adminUser.setRoles(roles);

				userRepository.save(adminUser);
				System.out.println("Usuario 'admin' creado con rol ROLE_ADMIN.");
			}
		};
	}
}






