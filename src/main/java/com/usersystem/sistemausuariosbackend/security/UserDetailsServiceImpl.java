package com.usersystem.sistemausuariosbackend.security;

import com.usersystem.sistemausuariosbackend.model.User;
import com.usersystem.sistemausuariosbackend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collection;
import java.util.stream.Collectors;

@Service // Indica que esta clase es un servicio de Spring
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    // Inyección de dependencias para el UserRepository
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Busca el usuario en la base de datos por su nombre de usuario o email
        User user = userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + username)));

        // Convierte los roles del usuario a una colección de GrantedAuthority para Spring Security
        Collection<? extends GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        // Retorna un objeto UserDetails de Spring Security
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(), // Estado de habilitación del usuario
                true, // Account non expired
                true, // Credentials non expired
                true, // Account non locked
                authorities // Roles/Permisos del usuario
        );
    }
}