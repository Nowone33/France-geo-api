package fr.nowone.francegeoapi.domain.ports.primary;

public interface UserPrimaryPort {

    void register(String email, String rawPassword, String firstName, String lastName);

    String login(String email, String rawPassword);

    void deleteAccount(String email);
}
