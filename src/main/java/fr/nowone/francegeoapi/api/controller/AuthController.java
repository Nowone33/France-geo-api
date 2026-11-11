package fr.nowone.francegeoapi.api.controller;

import fr.nowone.francegeoapi.api.dto.NewUserDTO;
import fr.nowone.francegeoapi.domain.ports.primary.UserPrimaryPort;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserPrimaryPort userPrimaryPort;
    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    public AuthController(UserPrimaryPort userPrimaryPort) {
        this.userPrimaryPort = userPrimaryPort;
    }

    @PostMapping("/signUp")
    public ResponseEntity<String> signUp(@Valid @RequestBody NewUserDTO newUser) {
        try {
            userPrimaryPort.register(
                    newUser.getEmail(),
                    newUser.getPassword(),
                    newUser.getFirstName(),
                    newUser.getLastName());
            return ResponseEntity.status(HttpStatus.CREATED).body("Compte créé avec succès");
        } catch(IllegalArgumentException e) {
            LOG.error("[AuthController] - Echec lors de la création du compte : {}.", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Echec lors de la création du compte");
        }
    }


    public record LoginRequest(String email, String password) {}

    @PostMapping("/signIn")
    public ResponseEntity<String> signIn(@RequestBody LoginRequest loginRequest) {
        try{
            String token = userPrimaryPort.login(loginRequest.email(), loginRequest.password());
            return ResponseEntity.ok(token);
        }catch( BadCredentialsException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Information de connexion invalide");
        }
    }

    //  (suppression du token du localStorage). Pour invalider côté serveur → blacklist Redis (Phase 3)
    @PostMapping("/signOut")
    public ResponseEntity<String> signOut() {
        return ResponseEntity.ok("Déconnexion réussie. Supprimez le token côté client.");

    }

    // Methode de suppression de compte
    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@AuthenticationPrincipal UserDetails userDetails) {
        userPrimaryPort.deleteAccount(userDetails.getUsername());
        return ResponseEntity.ok("Compte désactivé.");
    }
}
