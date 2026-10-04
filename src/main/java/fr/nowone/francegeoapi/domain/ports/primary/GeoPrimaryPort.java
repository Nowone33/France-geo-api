package fr.nowone.francegeoapi.domain.ports.primary;

import fr.nowone.francegeoapi.domain.model.PointCoordonnee;
import fr.nowone.francegeoapi.domain.model.ZoneGeographique;

import java.util.List;

public interface GeoPrimaryPort {
    ZoneGeographique findCommuneAtPoint(PointCoordonnee point);
    List<ZoneGeographique> findAllRegions();
    List<ZoneGeographique> findChildren(String parentCode, String type);
    void updateAllPopulation();
}
