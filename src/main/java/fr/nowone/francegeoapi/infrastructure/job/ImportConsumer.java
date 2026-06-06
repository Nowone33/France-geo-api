package fr.nowone.francegeoapi.infrastructure.job;

import fr.nowone.francegeoapi.api.dto.ImportMessage;
import fr.nowone.francegeoapi.domain.service.GeoImportService;
import fr.nowone.francegeoapi.infrastructure.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ImportConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportConsumer.class);
    private final GeoImportService geoImportService;

    public ImportConsumer(GeoImportService geoImportService) {
        this.geoImportService = geoImportService;
    }

    @RabbitListener(queues = RabbitMQConfig.IMPORT_QUEUE)
    public void handleImportMessage(ImportMessage message){
        LOGGER.info("[!] Début du traitement : {} pour {}", message.getType(), message.getParentCode());

        if(message.getParentCode() != null){
            LOGGER.info("Pour le parent : {}", message.getParentCode());
        }

        try{
            switch (message.getType()){
                case "REGION" -> geoImportService.importRegions();
                case "DEPARTMENT" -> geoImportService.importDepartments();
                case "COMMUNE" -> geoImportService.importCommunes(message.getParentCode());
                default -> LOGGER.warn("[?] Type d'import inconnu : {}", message.getType());
            }
            LOGGER.info("[O] Import réussi pour : {}", message.getType());
        } catch (Exception e) {

            LOGGER.error("[X] ÉCHEC de l'import pour le type {} (Parent: {}). Raison : {}",
                    message.getType(), message.getParentCode(), e.getMessage());
        }
    }
}
