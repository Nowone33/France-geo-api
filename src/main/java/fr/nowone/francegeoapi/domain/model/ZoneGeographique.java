package fr.nowone.francegeoapi.domain.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.Map;

@Value.Immutable
@JsonSerialize(as = ImmutableZoneGeographique.class)
@JsonDeserialize(as = ImmutableZoneGeographique.class)
public interface ZoneGeographique {
    String getId();
    String getCode();
    String getNom();
    String getType();
    @Nullable
    String getParentCode();
    Map<String, Object> getGeometrie();
    @Nullable
    Long getPopulation();
}
