package fr.nowone.francegeoapi.infrastructure.adapters;

import fr.nowone.francegeoapi.domain.model.ZoneGeographique;
import fr.nowone.francegeoapi.domain.ports.secondary.GeoSecondaryPort;
import fr.nowone.francegeoapi.infrastructure.database.entity.ZoneGeographiqueEntity;
import fr.nowone.francegeoapi.infrastructure.database.repository.ZoneRepository;
import fr.nowone.francegeoapi.infrastructure.mapper.ZoneGeographiqueMapper;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GeoAdapters implements GeoSecondaryPort {

    private MongoTemplate mongoTemplate;
    private final ZoneRepository  zoneRepository;
    private final ZoneGeographiqueMapper mapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(GeoAdapters.class);
    public GeoAdapters(ZoneRepository zoneRepository,  ZoneGeographiqueMapper mapper, MongoTemplate mongoTemplate) {
        this.zoneRepository = zoneRepository;
        this.mapper = mapper;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public ZoneGeographique findCommuneAtPoint(String type, Point point) {
        ZoneGeographiqueEntity entity = zoneRepository.findAt(type, point.getX(), point.getY()).orElse(null);
        return mapper.toDomain(entity);
    }

    @Override
    public List<ZoneGeographique> findAllRegions(String type) {
        List<ZoneGeographiqueEntity> entities = zoneRepository.findByType(type);
        return entities.stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<ZoneGeographique> findChildren(String parentCode, String type) {
        List<ZoneGeographiqueEntity> entities = zoneRepository.findByParentCodeAndType(parentCode, type);
        return entities.stream().map(mapper::toDomain).toList();
    }

    public void updateAllPopulation(){
        updateLevel("COMMUNE", "DEPARTEMENT");

        LOGGER.info("Fin dul calcul Dé&partement. lancement du calcul dans les régions. ");
        updateLevel("DEPARTEMENT", "REGION");
    }


    private void updateLevel(String childType, String parentType){

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("type").is(childType)),
                Aggregation.group("parentCode").sum("population").as("totalPopulation")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(
                aggregation, "zones", Document.class
        );
        LOGGER.info("Nombre de résultats pour childType : {} : {}", childType, results.getMappedResults().size());

        for(Document document : results){
            String parentCode= document.getString("_id"); // L'ID du groupe est le parentCode
            Long total = document.get("totalPopulation", Long.class);
            LOGGER.info("Parent: {} - Population: {}", document.get("_id"), document.get("totalPopulation"));
            if(parentCode != null){
                Query query = new Query(Criteria.where("code").is(parentCode).and("type").is(parentType));
                Update update = new Update().set("population", total);
                mongoTemplate.updateFirst(query, update, "zones");
            }
        }
    }
}
