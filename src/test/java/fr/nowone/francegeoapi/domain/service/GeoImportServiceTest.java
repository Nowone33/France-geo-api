package fr.nowone.francegeoapi.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.nowone.francegeoapi.api.dto.ImportMessage;
import fr.nowone.francegeoapi.domain.model.ZoneGeographique;
import fr.nowone.francegeoapi.domain.ports.secondary.GeoSecondaryPort;
import fr.nowone.francegeoapi.infrastructure.config.RabbitMQConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeoImportServiceTest {
    @Mock
    private GeoSecondaryPort geoSecondaryPort;

    @Mock
    private RabbitTemplate rabbitTemplate;


    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    private ObjectMapper objectMapper;

    @InjectMocks
    private GeoImportService geoImportService;

    @BeforeEach
    void setUp() {
        // On utilise une vraie instance d'ObjectMapper pour éviter de sur-mocker les conversions JSON
        this.objectMapper = new ObjectMapper();
        this.geoImportService = new GeoImportService(webClient, geoSecondaryPort, objectMapper, rabbitTemplate);
    }

    /**
     * Méthode utilitaire pour configurer le comportement en chaîne du WebClient simulé
     */
    private void mockWebClientResponse(String mockJsonBody) {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(mockJsonBody));
    }

    @Nested
    @DisplayName("importRegions")
    class ImportRegionsTests {

        @Test
        @DisplayName("Should import regions with success and sent chain message")
        void shouldImportRegionsSuccessfully() {
            // Given : Un GeoJSON valide représentant une région
            String jsonRegions = """
                {
                  "type": "FeatureCollection",
                  "features": [
                    {
                      "type": "Feature",
                      "properties": { "code": "11", "nom": "Île-de-France" },
                      "geometry": { "type": "Polygon", "coordinates": [] }
                    }
                  ]
                }
                """;

            mockWebClientResponse(jsonRegions);

            // When
            geoImportService.importRegions();

            // Then : 1. Vérification que la région a été envoyée au port secondaire pour sauvegarde
            ArgumentCaptor<ZoneGeographique> zoneCaptor = ArgumentCaptor.forClass(ZoneGeographique.class);
            verify(geoSecondaryPort, times(1)).save(zoneCaptor.capture());

            ZoneGeographique savedZone = zoneCaptor.getValue();
            assertThat(savedZone.getCode()).isEqualTo("11");
            assertThat(savedZone.getNom()).isEqualTo("Île-de-France");
            assertThat(savedZone.getType()).isEqualTo("REGION");

            // Then : 2. Vérification de l'envoi du message RabbitMQ pour l'étape suivante (DEPARTEMENT)
            verify(rabbitTemplate, times(1)).convertAndSend(
                    eq(RabbitMQConfig.IMPORT_EXCHANGE),
                    eq(RabbitMQConfig.IMPORT_ROUTING_KEY),
                    any(ImportMessage.class)
            );
        }
    }

    @Nested
    @DisplayName("importDepartments")
    class ImportDepartmentsTests {

        @Test
        @DisplayName("Should mix data between .gouv et github, save it and launch commune import")
        void shouldImportDepartmentsSuccessfully() {
            // Given : Simuler le premier appel (Fichier GitHub pour les géométries)
            String githubJson = """
                {
                  "type": "FeatureCollection",
                  "features": [
                    {
                      "type": "Feature",
                      "properties": { "code": "44" },
                      "geometry": { "type": "Polygon", "coordinates": [] }
                    }
                  ]
                }
                """;

            // Simuler le deuxième appel (API Gouv officielle)
            String gouvJson = """
                [
                  { "code": "44", "nom": "Loire-Atlantique", "codeRegion": "52" }
                ]
                """;

            // Enchaîner les deux réponses successives du WebClient
            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(githubJson), Mono.just(gouvJson));

            // When
            geoImportService.importDepartments();

            // Then : Vérification de la création de la zone avec géométrie croisée
            ArgumentCaptor<ZoneGeographique> zoneCaptor = ArgumentCaptor.forClass(ZoneGeographique.class);
            verify(geoSecondaryPort, times(1)).save(zoneCaptor.capture());

            ZoneGeographique savedDept = zoneCaptor.getValue();
            assertThat(savedDept.getCode()).isEqualTo("44");
            assertThat(savedDept.getNom()).isEqualTo("Loire-Atlantique");
            assertThat(savedDept.getParentCode()).isEqualTo("52");
            assertThat(savedDept.getGeometrie()).isNotNull(); // Preuve que le croisement a fonctionné

            // Vérification de l'envoi du message RabbitMQ pour l'étape COMMUNE
            verify(rabbitTemplate, times(1)).convertAndSend(
                    eq(RabbitMQConfig.IMPORT_EXCHANGE),
                    eq(RabbitMQConfig.IMPORT_ROUTING_KEY),
                    any(ImportMessage.class)
            );
        }
    }

    @Nested
    @DisplayName("importCommunes")
    class ImportCommunesTests {

        @Test
        @DisplayName("Should import each commune of department with population size")
        void shouldImportCommunesSuccessfully() {
            // Given
            String codeDept = "44";
            String jsonCommunes = """
                {
                  "type": "FeatureCollection",
                  "features": [
                    {
                      "type": "Feature",
                      "properties": { "code": "44109", "nom": "Nantes", "population": 320000 },
                      "geometry": { "type": "Point", "coordinates": [] }
                    }
                  ]
                }
                """;

            mockWebClientResponse(jsonCommunes);

            // When
            geoImportService.importCommunes(codeDept);

            // Then
            ArgumentCaptor<ZoneGeographique> zoneCaptor = ArgumentCaptor.forClass(ZoneGeographique.class);
            verify(geoSecondaryPort, times(1)).save(zoneCaptor.capture());

            ZoneGeographique savedCommune = zoneCaptor.getValue();
            assertThat(savedCommune.getCode()).isEqualTo("44109");
            assertThat(savedCommune.getNom()).isEqualTo("Nantes");
            assertThat(savedCommune.getType()).isEqualTo("COMMUNE");
            assertThat(savedCommune.getParentCode()).isEqualTo(codeDept);
            assertThat(savedCommune.getPopulation()).isEqualTo(320000L);
        }
    }
}