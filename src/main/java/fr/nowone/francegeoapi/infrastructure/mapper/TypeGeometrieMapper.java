package fr.nowone.francegeoapi.infrastructure.mapper;

import fr.nowone.francegeoapi.domain.model.TypeGeometrie;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TypeGeometrieMapper {
    default TypeGeometrie toTypeGeometrie(String type) {
        return type != null ? TypeGeometrie.fromValue(type) : null;
    }

    default String toString(TypeGeometrie type) {
        return type != null ? type.getValue() : null;
    }
}
