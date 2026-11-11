package fr.nowone.francegeoapi.security;

import fr.nowone.francegeoapi.infrastructure.database.sql.entity.UserEntity;
import fr.nowone.francegeoapi.infrastructure.database.sql.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));

        // Transformer nos roles metier en GrantedAuthority pour Spring Secu
        var authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());

        // retourne un type User de spring security
        return User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(authorities)
                .disabled(!user.isEnabled())
                .build();
    }
}
