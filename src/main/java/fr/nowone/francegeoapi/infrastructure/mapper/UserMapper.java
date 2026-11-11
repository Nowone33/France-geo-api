package fr.nowone.francegeoapi.infrastructure.mapper;

import fr.nowone.francegeoapi.domain.model.UserMetier;
import fr.nowone.francegeoapi.infrastructure.database.sql.entity.UserEntity;
import org.mapstruct.Mapper;


@Mapper(componentModel= "spring")
public interface UserMapper {
     UserMetier toDomain(UserEntity entity);
     UserEntity toEntity(UserMetier domain);
}

