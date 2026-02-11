package fr.nowone.francegeoapi.domain.service;

import fr.nowone.francegeoapi.domain.model.ZoneGeographique;
import fr.nowone.francegeoapi.domain.ports.primary.GeoPrimaryPort;
import fr.nowone.francegeoapi.domain.ports.secondary.GeoSecondaryPort;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeoService implements GeoPrimaryPort {

    private final GeoSecondaryPort geoSecondaryPort;

    public GeoService(GeoSecondaryPort geoSecondaryPort) {
        this.geoSecondaryPort = geoSecondaryPort;
    }

    @Override
    public ZoneGeographique findCommuneAtPoint(Point point) {
        String type = "COMMUNE";
        return geoSecondaryPort.findCommuneAtPoint(type, point);
    }

    @Override
    public List<ZoneGeographique> findAllRegions() {
        String type = "REGION";
        return geoSecondaryPort.findAllRegions(type);
    }

    @Override
    public List<ZoneGeographique> findChildren(String parentCode, String type) {
        return geoSecondaryPort.findChildren(parentCode, type);
    }

    @Override
    public void updateAllPopulation() {
        geoSecondaryPort.updateAllPopulation();
    }


}
