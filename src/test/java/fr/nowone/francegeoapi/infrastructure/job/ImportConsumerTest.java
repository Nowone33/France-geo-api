package fr.nowone.francegeoapi.infrastructure.job;

import fr.nowone.francegeoapi.api.dto.ImmutableImportMessage;
import fr.nowone.francegeoapi.api.dto.ImportMessage;
import fr.nowone.francegeoapi.domain.service.GeoImportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImportConsumerTest {

    @Mock
    private GeoImportService geoImportService;

    @InjectMocks
    private ImportConsumer importConsumer;

    @Nested
    @DisplayName("Tests d'aiguillage (Switch-case)")
    class RoutingTests {

        @Test
        @DisplayName("REGION - Should call importRegions")
        void handleImportMessage_WithRegion_ShouldCallImportRegions() {
            // Given
            ImportMessage message = ImmutableImportMessage.builder()
                    .type("REGION")
                    .parentCode(null)
                    .build();

            // When
            importConsumer.handleImportMessage(message);

            // Then
            verify(geoImportService, times(1)).importRegions();
            verifyNoMoreInteractions(geoImportService);
        }

        @Test
        @DisplayName("DEPARTMENT - Should call importDepartments")
        void handleImportMessage_WithDepartment_ShouldCallImportDepartments() {
            // Given
            ImportMessage message = ImmutableImportMessage.builder()
                    .type("DEPARTMENT")
                    .parentCode(null)
                    .build();

            // When
            importConsumer.handleImportMessage(message);

            // Then
            verify(geoImportService, times(1)).importDepartments();
            verifyNoMoreInteractions(geoImportService);
        }

        @Test
        @DisplayName("COMMUNE - should call importCommunes with good parentCode")
        void handleImportMessage_WithCommune_ShouldCallImportCommunesWithParent() {
            // Given
            String parentCode = "33"; // Gironde par exemple
            ImportMessage message = ImmutableImportMessage.builder()
                    .type("COMMUNE")
                    .parentCode(parentCode)
                    .build();

            // When
            importConsumer.handleImportMessage(message);

            // Then
            verify(geoImportService, times(1)).importCommunes(parentCode);
            verifyNoMoreInteractions(geoImportService);
        }

        @Test
        @DisplayName("Type inconnu - Ne doit appeler aucune méthode du service")
        void handleImportMessage_WithUnknownType_ShouldDoNothing() {
            // Given
            ImportMessage message = ImmutableImportMessage.builder()
                    .type("UNKNOWN_XYZ")
                    .parentCode("123")
                    .build();
            // When
            importConsumer.handleImportMessage(message);

            // Then
            // On s'assure qu'aucune méthode de notre service métier n'a été déclenchée
            verifyNoInteractions(geoImportService);
        }
    }

    @Nested
    @DisplayName("Erreur handling")
    class ExceptionTests {

        @Test
        @DisplayName("Exception - Should captur Error without throwing it (Pas de crash du Consumer)")
        void handleImportMessage_WhenServiceThrowsException_ShouldCatchAndLog() {
            // Given
            ImportMessage message = ImmutableImportMessage.builder()
                    .type("REGION")
                    .parentCode("")
                    .build();

            // On force le service à lever une erreur lors de l'appel
            doThrow(new RuntimeException("Erreur de connexion API externe"))
                    .when(geoImportService).importRegions();

            // When & Then
            // On appelle la méthode normalement. Si l'exception n'était pas gérée (pas de try-catch),
            // le test planterait ici. Le fait qu'il passe valide que le bloc catch fonctionne.
            importConsumer.handleImportMessage(message);

            verify(geoImportService, times(1)).importRegions();
        }
    }
}