package fr.nowone.francegeoapi.infrastructure.mapper;

import fr.nowone.francegeoapi.domain.model.ZoneGeographique;
import fr.nowone.francegeoapi.infrastructure.database.entity.ZoneGeographiqueEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ZoneGeographiqueMapper {

    ZoneGeographique toDomain(ZoneGeographiqueEntity entity);

    ZoneGeographiqueEntity toEntity(ZoneGeographique zoneGeographique);
}
