package fr.nowone.francegeoapi.domain.model;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value.Immutable
@JsonSerialize(as = ImmutableFavouriteCommune.class)
@JsonDeserialize(as = ImmutableFavouriteCommune.class)
public interface FavouriteCommune {
    UUID getId();
    UserMetier getUserMetier();
    String getCommuneCode();
    LocalDateTime getAddedAt();
}
