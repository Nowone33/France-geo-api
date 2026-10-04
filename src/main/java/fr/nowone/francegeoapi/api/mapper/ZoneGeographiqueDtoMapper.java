package fr.nowone.francegeoapi.api.mapper;

import fr.nowone.francegeoapi.api.dto.ZoneGeographiqueDto;
import fr.nowone.francegeoapi.domain.model.TypeGeometrie;
import fr.nowone.francegeoapi.domain.model.ZoneGeographique;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {TypeGeometrie.class})
public interface ZoneGeographiqueDtoMapper {

    ZoneGeographiqueDto toDto (ZoneGeographique model);

}
