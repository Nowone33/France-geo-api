package fr.nowone.francegeoapi.domain.service;

import fr.nowone.francegeoapi.domain.model.Role;
import fr.nowone.francegeoapi.domain.ports.primary.UserPrimaryPort;
import fr.nowone.francegeoapi.infrastructure.database.sql.entity.RoleEntity;
import fr.nowone.francegeoapi.infrastructure.database.sql.entity.UserEntity;
import fr.nowone.francegeoapi.infrastructure.database.sql.repository.RoleRepository;
import fr.nowone.francegeoapi.infrastructure.database.sql.repository.UserRepository;
import fr.nowone.francegeoapi.security.JwtUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
  /*
    Je suppose que je vais avoir besoin d'un service qui va gérer

    - [Inscription] tout ce qui hachage des mots de passe lors des inscriptions d'un nouvel utilisateur
    - [Connexion] accorder la connexion apres vérification du mot de passe
    - [Suppression] gérer la demande de suppression d'un utilisateur
    - [Desactivation] gérer la desactivation d'un utilisateur par un admin
    - [Favoris - Alerte] gérer la récupération d'un utilisateur par son token
    - [Favoris - Alerte] gérer la récupération d'un utilisateur par son mot de passe

     */

@Service
public class UserService implements UserPrimaryPort {



    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public UserService (UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;

    }

    @Override
    @Transactional
    public void register(String email, String rawPassword, String firstName, String lastName) {
        if(userRepository.existsByEmail(email)){
            throw new IllegalArgumentException("Il y a deja un compte associé à cet email");
        }
        String hashedPassword = passwordEncoder.encode(rawPassword);
        // A virer (les roles sont inscrits avec un script en BDD) un nouvel utilisateur sera set par défaut avec le role user.
        // L'admin via son dashboard pourra ensuite faire une elevation de privilège.
        RoleEntity defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("ROLE_USER absent de la base de donnée"));
        // 4. Crée et sauvegarde l'entité
        UserEntity newUser = new UserEntity();
        newUser.setEmail(email);
        newUser.setPasswordHash(hashedPassword);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setRoles(Set.of(defaultRole));
        userRepository.save(newUser);
    }

    @Override
    public String login(String email, String rawPassword) {
        UserEntity user =  userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Informations de connexion invalides"));
        if(!user.isEnabled()){
            throw new BadCredentialsException("Ce compte est désactivé");
        }
        return jwtUtils.generateToken(email);
    }

    @Override
    @Transactional
    public void deleteAccount(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        user.setEnabled(false);
        userRepository.delete(user);
    }

    @Transactional
    public void disableAccount(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        user.setEnabled(false);
        userRepository.save(user);
    }

}
