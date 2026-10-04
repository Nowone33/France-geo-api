package fr.nowone.francegeoapi.domain.model;

import org.immutables.value.Value;

@Value.Immutable
public interface PointCoordonnee {
    double getLatitude();
    double getLongitude();
}
