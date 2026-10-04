package fr.nowone.francegeoapi.api.controller;


import fr.nowone.francegeoapi.api.dto.ZoneGeographiqueDto;
import fr.nowone.francegeoapi.api.mapper.ZoneGeographiqueDtoMapper;
import fr.nowone.francegeoapi.domain.model.ImmutablePointCoordonnee;
import fr.nowone.francegeoapi.domain.model.PointCoordonnee;
import fr.nowone.francegeoapi.domain.model.ZoneGeographique;
import fr.nowone.francegeoapi.domain.ports.primary.GeoPrimaryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final ZoneGeographiqueDtoMapper mapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(GeoController.class.getName());

    public GeoController(GeoPrimaryPort geoPrimaryPort,  ZoneGeographiqueDtoMapper mapper) {
        this.geoPrimaryPort = geoPrimaryPort;
        this.mapper = mapper;
    }

    @GetMapping("/find")
    public ResponseEntity<ZoneGeographiqueDto> getZoneAtPoint(@RequestParam double latitude, @RequestParam double longitude) {
        LOGGER.info("[CONTROLLER GEO] Request to find a Zone with longitude {} and latitude {}", longitude, latitude);
        PointCoordonnee point = ImmutablePointCoordonnee.builder()
                .longitude(longitude)
                .latitude(latitude)
                .build();
       ZoneGeographiqueDto zone = mapper.toDto(geoPrimaryPort.findCommuneAtPoint(point));
       if(zone !=  null){
           LOGGER.info("[CONTROLLER GEO] Zone find is : {}", zone.nom());
       }
        return (zone != null)
                ? new ResponseEntity<>(zone, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @GetMapping("/regions")
    public ResponseEntity<List<ZoneGeographiqueDto>> getRegions() {
        LOGGER.info("[CONTROLLER GEO] Request to find regions Zones");
        List<ZoneGeographique> regionsModels = geoPrimaryPort.findAllRegions();

        if(ObjectUtils.isEmpty(regionsModels)) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        List<ZoneGeographiqueDto> regions = regionsModels.stream()
                .map(mapper::toDto)
                .toList();
        return ResponseEntity.ok(regions);
    }

    @GetMapping("/children/{parentCode}")
    public ResponseEntity<List<ZoneGeographiqueDto>> getChildren(@PathVariable String parentCode,
                                                              @RequestParam String expectedType) {
        LOGGER.info("[CONTROLLER GEO] Request to find children Zones of {} and Type of {}", parentCode, expectedType);

        List<ZoneGeographique> zonesModel = geoPrimaryPort.findChildren(parentCode, expectedType);
        if(ObjectUtils.isEmpty(zonesModel)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<ZoneGeographiqueDto> zones = zonesModel.stream()
                .map(mapper::toDto)
                .toList();
        return ResponseEntity.ok(zones);
    }
}


