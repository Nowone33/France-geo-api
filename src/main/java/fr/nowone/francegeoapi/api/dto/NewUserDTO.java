package fr.nowone.francegeoapi.api.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.immutables.value.Value;

@Value.Immutable
public interface NewUserDTO {

    @NotBlank
    @Email
    String getEmail();

    @NotBlank
    @Size(min = 8, message="Minimum 8 caractères")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).+$", message = "1 majuscule et 1 chiffre requis")
    String getPassword();

    @NotBlank
    String getFirstName();

    @NotBlank
    String getLastName();
}
