package fr.nowone.francegeoapi.api.controller;

import fr.nowone.francegeoapi.api.dto.ImmutableImportMessage;
import fr.nowone.francegeoapi.api.dto.ImportMessage;
import fr.nowone.francegeoapi.domain.ports.primary.GeoPrimaryPort;
import fr.nowone.francegeoapi.infrastructure.config.RabbitMQConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    @MockitoBean
    private GeoPrimaryPort geoPrimaryPort;

    @Nested
    @DisplayName("GET /launch-import")
    class LaunchImportTests {

        @Test
        @DisplayName("Should send a message RabbitMQ and send back a success message")
        void shouldLaunchImportSuccessfully() throws Exception {
            String typeParam = "COMMUNE";
            ImportMessage expectedMessage = ImmutableImportMessage.builder()
                    .type(typeParam)
                    .build();

            mockMvc.perform(get("/api/admin/launch-import")
                            .param("type", typeParam)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Ordre d'importation envoyé pour : " + typeParam));

            verify(rabbitTemplate).convertAndSend(
                    RabbitMQConfig.IMPORT_EXCHANGE,
                    RabbitMQConfig.IMPORT_ROUTING_KEY,
                    expectedMessage
            );
        }
    }

    @Nested
    @DisplayName("Get /compute-population")
    class ComputePopulationTests {

        @Test
        @DisplayName("Should call service to calcule population")
        void shouldComputePopulationSuccessfully() throws Exception {
            doNothing().when(geoPrimaryPort).updateAllPopulation();

            mockMvc.perform(get("/api/admin/compute-population")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Calcul des populations terminé !"));

            verify(geoPrimaryPort).updateAllPopulation();
        }
    }
}
