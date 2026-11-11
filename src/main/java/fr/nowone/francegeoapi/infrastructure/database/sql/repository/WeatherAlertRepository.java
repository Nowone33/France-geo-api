package fr.nowone.francegeoapi.infrastructure.database.sql.repository;

import fr.nowone.francegeoapi.infrastructure.database.sql.entity.WeatherAlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WeatherAlertRepository extends JpaRepository<WeatherAlertEntity, UUID> {
    WeatherAlertEntity findByUserIdAndCommuneCode(UUID userId, String communeCode);
    WeatherAlertEntity findByUserIdAndCommuneCodeAndTypeAlert(UUID userId, String communeCode, String typeAlert);
}
