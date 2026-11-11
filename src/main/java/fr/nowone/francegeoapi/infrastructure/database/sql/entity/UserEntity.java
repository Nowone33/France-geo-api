package fr.nowone.francegeoapi.infrastructure.database.sql.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name="users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true,  nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;
    private String firstName;
    private String lastName;
    private boolean enabled = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt =  LocalDateTime.now();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id")
    )
    private Set<RoleEntity> roles = new HashSet<>();

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FavouriteCommuneEntity> favouriteCommunes = new HashSet<>();


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WeatherAlertEntity> weatherAlerts = new HashSet<>();

    /*
     ***********         Constructor + Getters & Setters *************************************
     */

    public UserEntity() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<RoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleEntity> roles) {
        this.roles = roles;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
