package fr.nowone.francegeoapi.infrastructure.mapper;

import fr.nowone.francegeoapi.domain.model.Geometrie;
import fr.nowone.francegeoapi.domain.model.ZoneGeographique;
import fr.nowone.francegeoapi.infrastructure.database.entity.GeometrieEntity;
import fr.nowone.francegeoapi.infrastructure.database.entity.ZoneGeographiqueEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { TypeGeometrieMapper.class})
public interface ZoneGeographiqueMapper {

    ZoneGeographique toDomain(ZoneGeographiqueEntity entity);

    Geometrie toDomain (GeometrieEntity entity);

    ZoneGeographiqueEntity toEntity(ZoneGeographique zoneGeographique);

    GeometrieEntity toEntity(Geometrie geometrie);
}
