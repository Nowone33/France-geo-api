package fr.nowone.francegeoapi.infrastructure.database.sql.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="favourite_communes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "commune_code"})
)
public class FavouriteCommuneEntity {
    // un même user ne peut PAS ajouter 2x la même commune

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // Code INSEE de la commune
    @Column(name="commune_code", nullable = false)
    private String communeCode;

    @Column(name="added_at", updatable = false)
    LocalDateTime addedAt =  LocalDateTime.now();

    /*
        ***********         Constructor + Getters & Setters *************************************
     */

    public FavouriteCommuneEntity() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getCommuneCode() {
        return communeCode;
    }

    public void setCommuneCode(String communeCode) {
        this.communeCode = communeCode;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }
}
