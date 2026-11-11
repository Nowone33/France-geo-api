package fr.nowone.francegeoapi.infrastructure.database.sql.entity;


import fr.nowone.francegeoapi.domain.model.TypeAlert;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="weather_alert")
public class WeatherAlertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name ="commune_code", nullable = false)
    private String communeCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeAlert typeAlert;  // TEMPERATURE_MIN, TEMPERATURE_HOT, WIND_SPEED, RAIN, SNOW

    @Column(nullable = false)
    private double thresholdValue;

    @Column(name="is_active")
    private boolean isActive = true;

    @Column(name="last_triggered_at")
    LocalDateTime lastTriggeredAt;

    /*
     ***********         Constructor + Getters & Setters *************************************
     */

    public WeatherAlertEntity() {}

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

    public TypeAlert getTypeAlert() {
        return typeAlert;
    }

    public void setTypeAlert(TypeAlert typeAlert) {
        this.typeAlert = typeAlert;
    }

    public double getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(double thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getLastTriggeredAt() {
        return lastTriggeredAt;
    }

    public void setLastTriggeredAt(LocalDateTime lastTriggeredAt) {
        this.lastTriggeredAt = lastTriggeredAt;
    }
}
