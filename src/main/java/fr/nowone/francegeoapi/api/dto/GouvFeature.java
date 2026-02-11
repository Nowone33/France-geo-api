package fr.nowone.francegeoapi.api.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableGouvFeature.class)
@JsonDeserialize(as = ImmutableGouvFeature.class)
public interface GouvFeature {
    String getType(); // "Feature"
    JsonNode getGeometry();  // Contient type Polygon et coordinates
    JsonNode getProperties();// Contient les infos nom et code
}
