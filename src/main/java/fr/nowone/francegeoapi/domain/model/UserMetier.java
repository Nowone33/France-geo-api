package fr.nowone.francegeoapi.domain.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Value.Immutable
@JsonSerialize(as = ImmutableUserMetier.class)
@JsonDeserialize(as = ImmutableUserMetier.class)
public interface UserMetier {
    UUID getId();
    String getEmail();
    String getPassword();
    String getFirstName();
    String getLastName();
    boolean enabled();
    Set<Role> getRoles();
    LocalDateTime getCreated();
    @Nullable
    LocalDateTime getUpdated();
    @Nullable
    Set<FavouriteCommune> getFavouriteCommences();
    @Nullable
    Set<WeatherAlert> getWeatherAlerts();
}
