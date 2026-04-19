package fr.nowone.francegeoapi.infrastructure.job;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.nowone.francegeoapi.api.dto.GouvFeature;
import fr.nowone.francegeoapi.api.dto.GouvFeatureCollectionResponse;
import fr.nowone.francegeoapi.api.dto.ImmutableImportMessage;
import fr.nowone.francegeoapi.api.dto.ImportMessage;
import fr.nowone.francegeoapi.infrastructure.config.RabbitMQConfig;
import fr.nowone.francegeoapi.infrastructure.database.entity.ZoneGeographiqueEntity;
import fr.nowone.francegeoapi.infrastructure.database.repository.ZoneRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class GeoImportService {

    private final WebClient webClient;
    private final ZoneRepository zoneRepository;
    private static final Logger LOGGER = Logger.getLogger(GeoImportService.class.getName());
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    public GeoImportService(WebClient webClient, ZoneRepository zoneRepository, ObjectMapper objectMapper, RabbitTemplate rabbitTemplate) {
        this.webClient = webClient;
        this.zoneRepository = zoneRepository;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
    }


    public void importRegions()  {
        try{
            //1 Appel API
            LOGGER.info("[!] Appel à l'API pour les régions...");
            String rawJson = webClient.get()
                    .uri("https://raw.githubusercontent.com/gregoiredavid/france-geojson/master/regions-version-simplifiee.geojson")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            GouvFeatureCollectionResponse response = objectMapper.readValue(rawJson, GouvFeatureCollectionResponse.class);

            if(response != null && response.getFeatures() != null) {
                LOGGER.info("[O] " + response.getFeatures().size() + " régions récupérées.");

                response.getFeatures().forEach(feature -> {
                    ZoneGeographiqueEntity zone =  new ZoneGeographiqueEntity();

                    JsonNode props = feature.getProperties();
                    zone.setNom(props.get("nom").asText());
                    zone.setCode(props.get("code").asText());
                    zone.setType("REGION");
                    Map<String, Object> geoMap = objectMapper.convertValue(feature.getGeometry(), Map.class);
                    zone.setGeometrie(geoMap);
                    zoneRepository.save(zone);

                });

                // On lance l'ordre de mission pour les départements de cette région
                ImportMessage message = ImmutableImportMessage.builder()
                        .type("DEPARTEMENT")
                        .build();

                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.IMPORT_EXCHANGE,
                        RabbitMQConfig.IMPORT_ROUTING_KEY,
                        message
                );
            }
        } catch (Exception ex) {
            LOGGER.severe("[X] Erreur critique lors de l'importation des régions : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void importDepartments()  {
        String url = "https://raw.githubusercontent.com/gregoiredavid/france-geojson/master/departements-version-simplifiee.geojson";
        try {
            LOGGER.info("[!] Appel à l'API GIT HUB pour les coordonnées départements...");
            String geoJsonRaw = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            GouvFeatureCollectionResponse geoSource = objectMapper.readValue(geoJsonRaw, GouvFeatureCollectionResponse.class);

            // Création d'un index (Code -> Geométrie)
            Map<String, JsonNode> geoMap = geoSource.getFeatures().stream()
                    .collect(Collectors.toMap(
                            dep -> dep.getProperties().get("code").asText(),
                            GouvFeature::getGeometry
                    ));

            //Récupération des départements contenant le code région sur l'api gouv
            String officialRaw = webClient.get()
                    .uri("https://geo.api.gouv.fr/departements")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode departementsOfficiels = objectMapper.readTree(officialRaw);

            //Croisement des données
            for(JsonNode node:  departementsOfficiels) {
                String code = node.get("code").asText();

                ZoneGeographiqueEntity depts = new ZoneGeographiqueEntity();
                depts.setCode(code);
                depts.setNom(node.get("nom").asText());
                depts.setParentCode(node.get("codeRegion").asText());
                depts.setType("DEPARTEMENT");

                if(geoMap.containsKey(code)){
                    Map<String, Object> geometry = objectMapper.convertValue(geoMap.get(code), Map.class);
                    depts.setGeometrie(geometry);
                }
                zoneRepository.save(depts);

                // Lancement de l'appel des communes
                ImportMessage message = ImmutableImportMessage.builder()
                        .type("COMMUNE")
                        .parentCode(code)
                        .build();

                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.IMPORT_EXCHANGE,
                        RabbitMQConfig.IMPORT_ROUTING_KEY,
                        message
                );
            }
        } catch (Exception ex){
            LOGGER.severe("[X] Erreur critique lors du croisement des données des départements: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void importCommunes(String codeDepartement)  {
        String url = "https://geo.api.gouv.fr/departements/" + codeDepartement + "/communes?format=geojson&geometry=contour";

        try {
            LOGGER.info("[!] Appel à l'API gouv pour les communes...");
            String  jsonRaw = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            GouvFeatureCollectionResponse response = objectMapper.readValue(jsonRaw, GouvFeatureCollectionResponse.class);

            if(response != null && response.getFeatures() != null){
                LOGGER.info("[O] " + response.getFeatures().size() + " communes récupérées.");
                response.getFeatures().forEach(feature -> {
                    ZoneGeographiqueEntity commune = new ZoneGeographiqueEntity();

                    JsonNode props = feature.getProperties();
                    commune.setNom(props.get("nom").asText());
                    commune.setCode(props.get("code").asText());
                    commune.setType("COMMUNE");
                    commune.setParentCode(codeDepartement);
                    Map<String, Object> geoMap = objectMapper.convertValue(feature.getGeometry(), Map.class);
                    commune.setGeometrie(geoMap);
                    commune.setPopulation(props.has("population")?props.get("population").asLong() : 0L);
                    zoneRepository.save(commune);
                });
            }

        }catch (Exception ex){
            LOGGER.severe("[X] Erreur critique lors de la récupération des communes: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
