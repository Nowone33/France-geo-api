package fr.nowone.francegeoapi.infrastructure.mapper;

import fr.nowone.francegeoapi.domain.model.FavouriteCommune;
import fr.nowone.francegeoapi.infrastructure.database.sql.entity.FavouriteCommuneEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FavouriteMapper {
    FavouriteCommuneEntity toEntity(FavouriteCommune domain);
    FavouriteCommune toDomain(FavouriteCommuneEntity entity);
}
