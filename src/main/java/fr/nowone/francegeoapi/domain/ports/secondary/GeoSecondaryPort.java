package fr.nowone.francegeoapi.domain.ports.secondary;

import fr.nowone.francegeoapi.domain.model.ZoneGeographique;
import org.springframework.data.geo.Point;

import java.util.List;

public interface GeoSecondaryPort {
    ZoneGeographique findCommuneAtPoint(String type, Point point);
    List<ZoneGeographique> findAllRegions(String type);
    List<ZoneGeographique> findChildren(String parentCode, String type);
    void updateAllPopulation();
    void save(ZoneGeographique zoneGeographique);
}
