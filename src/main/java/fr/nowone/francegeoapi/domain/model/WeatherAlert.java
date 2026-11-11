package fr.nowone.francegeoapi.domain.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value.Immutable
@JsonSerialize(as = ImmutableWeatherAlert.class)
@JsonDeserialize(as = ImmutableWeatherAlert.class)
public interface WeatherAlert {
    UUID getId();
    UserMetier getUser();
    String getCommuneCode();
    TypeAlert getTypeAlert();
    double getTresholdValue();
    boolean isActive();
    LocalDateTime getLastTriggerAt();
}
