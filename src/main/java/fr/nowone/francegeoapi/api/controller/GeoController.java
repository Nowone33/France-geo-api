package fr.nowone.francegeoapi.api.controller;


import fr.nowone.francegeoapi.domain.model.ZoneGeographique;
import fr.nowone.francegeoapi.domain.ports.primary.GeoPrimaryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/zone")
@CrossOrigin(origins = "http://localhost:5173")
public class GeoController {

    private final GeoPrimaryPort geoPrimaryPort;
    private static final Logger LOGGER = LoggerFactory.getLogger(GeoController.class.getName());

    public GeoController(GeoPrimaryPort geoPrimaryPort) {
        this.geoPrimaryPort = geoPrimaryPort;
    }

    @GetMapping("/find")
    public ResponseEntity<ZoneGeographique> getZoneAtPoint(@RequestParam double latitude, @RequestParam double longitude) {
        LOGGER.info("[CONTROLLER GEO] Request to find a Zone with longitude {} and latitude {}", String.valueOf(longitude), String.valueOf(latitude));
        Point point = new Point(longitude, latitude);
       ZoneGeographique zone = geoPrimaryPort.findCommuneAtPoint(point);
       if(zone !=  null){
           LOGGER.info("[CONTROLLER GEO] Zone find is : {}", zone.getNom());
       }
        return (zone != null)
                ? new ResponseEntity<>(zone, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @GetMapping("/regions")
    public ResponseEntity<List<ZoneGeographique>> getRegions() {
        LOGGER.info("[CONTROLLER GEO] Request to find regions Zones");
        List<ZoneGeographique> regions = geoPrimaryPort.findAllRegions();

        if(ObjectUtils.isEmpty(regions)) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.ok(regions);
    }

    @GetMapping("/children/{parentCode}")
    public ResponseEntity<List<ZoneGeographique>> getChildren(@PathVariable String parentCode, @RequestParam String expectedType) {
        LOGGER.info("[CONTROLLER GEO] Request to find children Zones of {} and Type of {}", parentCode, expectedType);
        return ResponseEntity.ok(geoPrimaryPort.findChildren(parentCode, expectedType));
    }
}


