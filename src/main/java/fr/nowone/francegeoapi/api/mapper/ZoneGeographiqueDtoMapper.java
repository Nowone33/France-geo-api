package fr.nowone.francegeoapi.api.mapper;

import fr.nowone.francegeoapi.api.dto.ZoneGeographiqueDto;
import fr.nowone.francegeoapi.domain.model.TypeGeometrie;
import fr.nowone.francegeoapi.domain.model.ZoneGeographique;
import fr.nowone.francegeoapi.infrastructure.mapper.TypeGeometrieMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {TypeGeometrieMapper.class})
public interface ZoneGeographiqueDtoMapper {

    ZoneGeographiqueDto toDto (ZoneGeographique model);

}
