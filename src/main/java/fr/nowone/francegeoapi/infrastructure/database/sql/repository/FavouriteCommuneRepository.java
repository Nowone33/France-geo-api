package fr.nowone.francegeoapi.infrastructure.database.sql.repository;

import fr.nowone.francegeoapi.infrastructure.database.sql.entity.FavouriteCommuneEntity;
import fr.nowone.francegeoapi.infrastructure.database.sql.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FavouriteCommuneRepository extends JpaRepository<FavouriteCommuneEntity, UUID> {

    List<FavouriteCommuneEntity> findAllByUser_Id(UUID userId);
    boolean existsByUserAndCommuneCode(UserEntity user, String communeCode);
    void deleteByUserAndCommuneCode(UUID userId, String communeCode);
}
