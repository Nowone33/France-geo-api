package fr.nowone.francegeoapi.domain.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableGeometrie.class)
@JsonDeserialize(as = ImmutableGeometrie.class)
public interface Geometrie {
    TypeGeometrie getType();

    /**
     * Pour un POINT : 1 seul point (ou [lon, lat])
     * Pour un POLYGON : liste de rings -> liste de points
     * Pour un MULTI_POLYGON : liste de polygons -> liste de rings
     */
    Object getCoordinates();
}
