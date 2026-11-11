package fr.nowone.francegeoapi.infrastructure.mapper;

import fr.nowone.francegeoapi.domain.model.WeatherAlert;
import fr.nowone.francegeoapi.infrastructure.database.sql.entity.WeatherAlertEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AlertMapper {

    WeatherAlertEntity toEntity(WeatherAlert domain);
    WeatherAlert toDomain(WeatherAlertEntity entity);
}
