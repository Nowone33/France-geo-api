package fr.nowone.francegeoapi.domain.ports.primary;

import fr.nowone.francegeoapi.domain.model.ZoneGeographique;
import org.springframework.data.geo.Point;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface GeoPrimaryPort {
    ZoneGeographique findCommuneAtPoint(Point point);
    List<ZoneGeographique> findAllRegions();
    List<ZoneGeographique> findChildren(String parentCode, String type);
    void updateAllPopulation();
}
