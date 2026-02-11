package fr.nowone.francegeoapi.api.controller;


import fr.nowone.francegeoapi.api.dto.ImmutableImportMessage;
import fr.nowone.francegeoapi.api.dto.ImportMessage;
import fr.nowone.francegeoapi.domain.ports.primary.GeoPrimaryPort;
import fr.nowone.francegeoapi.infrastructure.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final RabbitTemplate rabbitTemplate;
    private final GeoPrimaryPort geoPrimaryPort;

    public AdminController(RabbitTemplate rabbitTemplate, GeoPrimaryPort geoPrimaryPort) {
        this.rabbitTemplate = rabbitTemplate;
        this.geoPrimaryPort = geoPrimaryPort;
    }

    @GetMapping("/launch-import")
    public String launchImport(@RequestParam String type) {
        ImportMessage message = ImmutableImportMessage.builder()
                .type(type)
                .build();
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.IMPORT_EXCHANGE,
                RabbitMQConfig.IMPORT_ROUTING_KEY,
                message);

        return "Ordre d'importation envoyé pour : " + type;
    }

    @GetMapping("/compute-population")
    public ResponseEntity<String> computePopulation() {
        geoPrimaryPort.updateAllPopulation();
        return ResponseEntity.ok("Calcul des populations terminé !");
    }

}
