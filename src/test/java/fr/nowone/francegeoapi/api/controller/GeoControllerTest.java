package fr.nowone.francegeoapi.api.controller;

import fr.nowone.francegeoapi.domain.model.ImmutableZoneGeographique;
import fr.nowone.francegeoapi.domain.model.ZoneGeographique;
import fr.nowone.francegeoapi.domain.ports.primary.GeoPrimaryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.geo.Point;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import java.util.Map;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(GeoController.class)
class GeoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GeoPrimaryPort geoService;

    private List<ZoneGeographique> regions;
    private List<ZoneGeographique> departements;


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
    @DisplayName("GET /zone/regions  (get regions)")
    class GetRegionsTests {

        @Test
        @DisplayName("Should send Region when triggered")
        void SuccessfullTest_getRegions() throws Exception {
            when(geoService.findAllRegions()).thenReturn(regions);
            mockMvc.perform(get("/zone/regions"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].nom").value("Ile-de-France"))
                    .andExpect(jsonPath("$[1].nom").value("Aquitaine"));
        }


        @Test
        @DisplayName("Should throw an Exception if service is down")
        void FailedTest_getRegions() throws Exception {
            when(geoService.findAllRegions()).thenReturn(null);
            mockMvc.perform(get("/zone/regions"))
                    .andExpect(status().is5xxServerError());
        }
    }

    @Nested
    @DisplayName("GET /zone/children (get departements)")
    class GetDepartementsTests {
        @Test
        @DisplayName("Should send Departements when triggered")
        void SuccessfullTest_getDepartements() throws Exception {
            String regionCode = "11";
            String expectedChildren = "DEPARTEMENT";
            when(geoService.findChildren(regionCode, expectedChildren)).thenReturn(departements);

            mockMvc.perform(get("/zone/children/" + regionCode)
                            .param("expectedType", expectedChildren))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].nom").value("Yvelines"))
                    .andExpect(jsonPath("$[1].nom").value("Seine et Marne"));
        }


        @Test
        @DisplayName("Sould throw an Exception if No Code")
        void FailedTest_getDepartementsNoCode() throws Exception {

            String expectedChildren = "DEPARTEMENT";
            mockMvc.perform(get("/zone/children/")
                            .param("expectedType", expectedChildren))
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("Sould throw an Exception if No param")
        void FailedTest_getDepartementsNoParam() throws Exception {
            String regionCode = "11";

            when(geoService.findChildren(regionCode, null)).thenReturn(null);
            mockMvc.perform(get("/zone/children/" + regionCode))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /zone/find (getZoneAtPoint)")
    class GetZoneAtPointTests {

        @Test
        @DisplayName("Should return 200 OK and the zone when a zone is found at coordinates")
        void shouldReturnZoneWhenFound() throws Exception {
            double latitude = 46.58;
            double longitude = 0.34;
            Point expectedPoint = new Point(longitude, latitude);

            ZoneGeographique mockZone = ImmutableZoneGeographique.builder()
                    .id("6987cb")
                    .code("86194")
                    .nom("Poitiers")
                    .type("COMMUNE")
                    .parentCode("86")
                    .geometrie(Map.of())
                    .population(89916L)
                    .build();

            when(geoService.findCommuneAtPoint(expectedPoint)).thenReturn(mockZone);

            mockMvc.perform(get("/zone/find")
                            .param("latitude", String.valueOf(latitude))
                            .param("longitude", String.valueOf(longitude))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nom").value("Poitiers"));

            verify(geoService, times(1)).findCommuneAtPoint(expectedPoint);
        }

        @Test
        @DisplayName("Should return 404 Not Found when no zone corresponds to coordinates")
        void shouldReturn404WhenNotFound() throws Exception {
            double latitude = 0.0;
            double longitude = 0.0;
            Point expectedPoint = new Point(longitude, latitude);

            when(geoService.findCommuneAtPoint(expectedPoint)).thenReturn(null);

            mockMvc.perform(get("/zone/find")
                            .param("latitude", String.valueOf(latitude))
                            .param("longitude", String.valueOf(longitude)))
                    .andExpect(status().isNotFound());

            verify(geoService, times(1)).findCommuneAtPoint(expectedPoint);
        }

        @Test
        @DisplayName("Should return 400 Bad Request when parameters are missing")
        void shouldReturn400WhenParamsMissing() throws Exception {

            mockMvc.perform(get("/zone/find"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(geoService);
        }
    }
}