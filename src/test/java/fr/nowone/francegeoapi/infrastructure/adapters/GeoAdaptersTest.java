package fr.nowone.francegeoapi.infrastructure.adapters;

import fr.nowone.francegeoapi.domain.model.ImmutableZoneGeographique;
import fr.nowone.francegeoapi.domain.model.ZoneGeographique;
import fr.nowone.francegeoapi.infrastructure.database.entity.ZoneGeographiqueEntity;
import fr.nowone.francegeoapi.infrastructure.database.repository.ZoneRepository;
import fr.nowone.francegeoapi.infrastructure.mapper.ZoneGeographiqueMapper;
import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeoAdaptersTest {

    @Mock
    private ZoneRepository zoneRepository;

    @Mock
    private ZoneGeographiqueMapper mapper;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private GeoAdapters geoAdapters;

    @Nested
    @DisplayName("Méthodes de requêtage (Queries)")
    class QueryTests {

        @Test
        @DisplayName("findCommuneAtPoint - Should return Commune if exist")
        void findCommuneAtPoint_ShouldReturnZone_WhenEntityExists() {
            // Given
            String type = "COMMUNE";
            Point point = new Point(2.3522, 48.8566);
            ZoneGeographiqueEntity mockEntity = new ZoneGeographiqueEntity();
            ZoneGeographique expectedDomain = ImmutableZoneGeographique.builder()
                    .id("6985ca")
                    .nom("Commune A")
                    .code("001")
                    .type("COMMUNE")
                    .geometrie(Map.of())
                    .parentCode("01")
                    .population(0L)
                    .build();

            when(zoneRepository.findAt(type, point.getX(), point.getY())).thenReturn(Optional.of(mockEntity));
            when(mapper.toDomain(mockEntity)).thenReturn(expectedDomain);

            // When
            ZoneGeographique result = geoAdapters.findCommuneAtPoint(type, point);

            // Then
            assertThat(result).isNotNull().isEqualTo(expectedDomain);
            verify(zoneRepository).findAt(type, point.getX(), point.getY());
            verify(mapper).toDomain(mockEntity);
        }

        @Test
        @DisplayName("findAllRegions - Should return mapped regions")
        void findAllRegions_ShouldReturnMappedList() {
            // Given
            String type = "REGION";
            ZoneGeographiqueEntity entity = new ZoneGeographiqueEntity();
            ZoneGeographique domain = ImmutableZoneGeographique.builder()
                    .id("6985ca")
                    .nom("Region A")
                    .code("001")
                    .type("REGION")
                    .geometrie(Map.of())
                    .parentCode("01")
                    .population(0L)
                    .build();

            when(zoneRepository.findByType(type)).thenReturn(List.of(entity));
            when(mapper.toDomain(entity)).thenReturn(domain);

            // When
            List<ZoneGeographique> results = geoAdapters.findAllRegions(type);

            // Then
            assertThat(results).hasSize(1).containsExactly(domain);
        }

        @Test
        @DisplayName("findChildren - Should return childrens of parent")
        void findChildren_ShouldReturnChildrenList() {
            // Given
            String parentCode = "33";
            String type = "COMMUNE";
            ZoneGeographiqueEntity entity = new ZoneGeographiqueEntity();
            ZoneGeographique domain = ImmutableZoneGeographique.builder()
                    .id("6985ca")
                    .nom("Commune A")
                    .code("33001")
                    .type("COMMUNE")
                    .geometrie(Map.of())
                    .parentCode("33")
                    .population(0L)
                    .build();

            when(zoneRepository.findByParentCodeAndType(parentCode, type)).thenReturn(List.of(entity));
            when(mapper.toDomain(entity)).thenReturn(domain);

            // When
            List<ZoneGeographique> results = geoAdapters.findChildren(parentCode, type);

            // Then
            assertThat(results).hasSize(1).containsExactly(domain);
        }
    }

    @Nested
    @DisplayName("Save and persist method (Commands)")
    class CommandTests {

        @Test
        @DisplayName("save - Should correctly map the domain and call repo")
        void save_ShouldMapAndSaveEntity() {
            // Given
            ZoneGeographique domain = ImmutableZoneGeographique.builder()
                    .id("6985cb")
                    .code("75")
                    .nom("Paris")
                    .type("DEPARTEMENT")
                    .parentCode("11")
                    .population(2100000L)
                    .build();

            ArgumentCaptor<ZoneGeographiqueEntity> entityCaptor = ArgumentCaptor.forClass(ZoneGeographiqueEntity.class);

            // When
            geoAdapters.save(domain);

            // Then
            verify(zoneRepository).save(entityCaptor.capture());
            ZoneGeographiqueEntity savedEntity = entityCaptor.getValue();

            assertThat(savedEntity.getCode()).isEqualTo("75");
            assertThat(savedEntity.getNom()).isEqualTo("Paris");
            assertThat(savedEntity.getType()).isEqualTo("DEPARTEMENT");
            assertThat(savedEntity.getParentCode()).isEqualTo("11");
            assertThat(savedEntity.getPopulation()).isEqualTo(2100000L);
        }

        @Test
        @DisplayName("updateAllPopulation - Should aggregate and update population size of parent")
        @SuppressWarnings("unchecked")
        void updateAllPopulation_ShouldAggregateAndUpdate() {
            // Given
            // Simulation du résultat de l'agrégation pour le premier passage (COMMUNE -> DEPARTEMENT)
            Document docDept = new Document("_id", "33").append("totalPopulation", 500000L);
            AggregationResults<Document> resultsDept = new AggregationResults<>(List.of(docDept), new Document());

            // Simulation pour le second passage (DEPARTEMENT -> REGION)
            Document docReg = new Document("_id", "75").append("totalPopulation", 2000000L);
            AggregationResults<Document> resultsReg = new AggregationResults<>(List.of(docReg), new Document());

            // Configuration des stubs pour MongoTemplate (on mock l'agrégation)
            when(mongoTemplate.aggregate(any(Aggregation.class), eq("zones"), eq(Document.class)))
                    .thenReturn(resultsDept) // 1er appel (COMMUNE)
                    .thenReturn(resultsReg);  // 2ème appel (DEPARTEMENT)

            // ArgumentCaptor pour inspecter les requêtes de mise à jour envoyées à Mongo
            ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
            ArgumentCaptor<Update> updateCaptor = ArgumentCaptor.forClass(Update.class);

            // When
            geoAdapters.updateAllPopulation();

            // Then
            // On vérifie que mongoTemplate.updateFirst a été appelé 2 fois au total
            verify(mongoTemplate, times(2)).updateFirst(queryCaptor.capture(), updateCaptor.capture(), eq("zones"));

            List<Query> capturedQueries = queryCaptor.getAllValues();
            List<Update> capturedUpdates = updateCaptor.getAllValues();

            // Vérification du premier traitement (Mise à jour du Département 33)
            assertThat(capturedQueries.get(0).toString()).contains("\"code\" : \"33\"").contains("\"type\" : \"DEPARTEMENT\"");
            assertThat(capturedUpdates.get(0).toString()).contains("500000");

            // Vérification du second traitement (Mise à jour de la Région 75)
            assertThat(capturedQueries.get(1).toString()).contains("\"code\" : \"75\"").contains("\"type\" : \"REGION\"");
            assertThat(capturedUpdates.get(1).toString()).contains("2000000");
        }
    }
}