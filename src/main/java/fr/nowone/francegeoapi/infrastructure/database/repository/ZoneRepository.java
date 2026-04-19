package fr.nowone.francegeoapi.infrastructure.database.repository;

import fr.nowone.francegeoapi.infrastructure.database.entity.ZoneGeographiqueEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ZoneRepository extends MongoRepository<ZoneGeographiqueEntity, String> {

    List<ZoneGeographiqueEntity> findByType(String type);

    List<ZoneGeographiqueEntity> findByParentCodeAndType(String parentCode, String type);

    @Query("{ 'type': ?0, 'geometrie': { $geoIntersects: { $geometry: { type: 'Point', coordinates: [ ?1, ?2 ] } } } }")
    Optional<ZoneGeographiqueEntity> findAt(String type, double longitude, double latitude);

}


