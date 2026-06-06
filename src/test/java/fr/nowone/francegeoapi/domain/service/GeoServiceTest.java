package fr.nowone.francegeoapi.domain.service;

import fr.nowone.francegeoapi.domain.model.ImmutableZoneGeographique;
import fr.nowone.francegeoapi.domain.model.ZoneGeographique;
import fr.nowone.francegeoapi.domain.ports.secondary.GeoSecondaryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.geo.Point;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeoServiceTest {

    @Mock
    private GeoSecondaryPort geoSecondaryPort;

    @InjectMocks
    private GeoService geoService;

    List<ZoneGeographique> regions;
    List<ZoneGeographique> departements;

    @BeforeEach
    void setUp() {

        ZoneGeographique ileDeFrance = ImmutableZoneGeographique.builder()
                .id("6985ca")
                .code("11")
                .nom("Ile-de-France")
                .type("REGION")
                .parentCode(null)
                .geometrie(Map.of())
                .population(12254L)
                .build();

        ZoneGeographique Aquitaine = ImmutableZoneGeographique.builder()
                .id("6985cb")
                .code("09")
                .nom("Aquitaine")
                .type("REGION")
                .parentCode(null)
                .geometrie(Map.of())
                .population(12054L)
                .build();

        regions= List.of(ileDeFrance,Aquitaine);

        ZoneGeographique yveline = ImmutableZoneGeographique.builder()
                .id("6986ca")
                .code("78")
                .nom("Yvelines")
                .type("DEPARTEMENT")
                .parentCode("11")
                .geometrie(Map.of())
                .population(1485086L)
                .build();

        ZoneGeographique seineEtMarne = ImmutableZoneGeographique.builder()
                .id("6986cb")
                .code("77")
                .nom("Seine et Marne")
                .type("DEPARTEMENT")
                .parentCode("11")
                .geometrie(Map.of())
                .population(1468108L)
                .build();

        departements = List.of(yveline,seineEtMarne);

    }


    @Nested
    @DisplayName("findCommuneAtPoint")
    class FindCommuneAtPointTests {

        @Test
        @DisplayName("Should call secondary port with type COMMUNE send back finded zone")
        void shouldReturnCommuneAtPoint() {
            // Given
            Point point = new Point(2.3522, 48.8566);
            ZoneGeographique expectedZone = ImmutableZoneGeographique.builder()
                    .id("6985ca")
                    .nom("Paris")
                    .code("75056")
                    .type("COMMUNE")
                    .parentCode("75")
                    .geometrie(Map.of())
                    .population(2103778L)
                    .build();

            when(geoSecondaryPort.findCommuneAtPoint("COMMUNE", point)).thenReturn(expectedZone);
            ZoneGeographique result = geoService.findCommuneAtPoint(point);

            assertThat(result).isNotNull().isEqualTo(expectedZone);
            verify(geoSecondaryPort, times(1)).findCommuneAtPoint("COMMUNE", point);
        }
    }

    @Nested
    @DisplayName("findAllRegions")
    class FindAllRegionsTests {

        @Test
        @DisplayName("Should call secondaryPort with type REGION send back a list")
        void shouldReturnAllRegions() {

            when(geoSecondaryPort.findAllRegions("REGION")).thenReturn(regions);

            List<ZoneGeographique> result = geoService.findAllRegions();

            assertThat(result).isEqualTo(regions);
            verify(geoSecondaryPort, times(1)).findAllRegions("REGION");
        }
    }

    @Nested
    @DisplayName("findChildren")
    class FindChildrenTests {

        @Test
        @DisplayName("Should send correct message to secondaryPort")
        void shouldReturnChildrenZones() {
            String parentCode = "11";
            String type = "DEPARTEMENT";
            when(geoSecondaryPort.findChildren(parentCode, type)).thenReturn(departements);

            List<ZoneGeographique> result = geoService.findChildren(parentCode, type);

            assertThat(result).isEqualTo(departements);
            verify(geoSecondaryPort, times(1)).findChildren(parentCode, type);
        }
    }

    @Nested
    @DisplayName("updateAllPopulation")
    class UpdateAllPopulationTests {

        @Test
        @DisplayName("Should delegate pour the comute of population")
        void shouldDelegateUpdatePopulation() {// Given - Rien à stubber car la méthode est void
            doNothing().when(geoSecondaryPort).updateAllPopulation();

            geoService.updateAllPopulation();

            verify(geoSecondaryPort, times(1)).updateAllPopulation();
        }
    }

}