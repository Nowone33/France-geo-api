package fr.nowone.francegeoapi.domain.service;

import fr.nowone.francegeoapi.domain.model.FavouriteCommune;
import fr.nowone.francegeoapi.domain.model.TypeAlert;
import fr.nowone.francegeoapi.domain.ports.primary.PreferencesPrimaryPort;
import fr.nowone.francegeoapi.infrastructure.database.sql.entity.FavouriteCommuneEntity;
import fr.nowone.francegeoapi.infrastructure.database.sql.entity.UserEntity;
import fr.nowone.francegeoapi.infrastructure.database.sql.entity.WeatherAlertEntity;
import fr.nowone.francegeoapi.infrastructure.database.sql.repository.FavouriteCommuneRepository;
import fr.nowone.francegeoapi.infrastructure.database.sql.repository.UserRepository;
import fr.nowone.francegeoapi.infrastructure.database.sql.repository.WeatherAlertRepository;
import fr.nowone.francegeoapi.infrastructure.mapper.FavouriteMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
       Dans ce service on va gérer tout ce qui à trait à la gestion des favoris et des alertes

       - ajout d'une commune aux favoris d'un utilisateur
       - recuperation des communes favorites d'un utilisateur
       - suppression d'une commune des favorites d'un utilisateur

       - association d'une alerte sur une des communes favorites de l'utilisateur
       - modification de l'alerte
       - desactivation de l'alerte (peut être entendu comme une modification)
       - suppression de l'alerte

    */
@Service
public class PreferencesService implements PreferencesPrimaryPort {

    private final UserRepository userRepository;
    private final FavouriteCommuneRepository favouriteRepository;
    private final WeatherAlertRepository alertRepository;
    private final FavouriteMapper favouriteMapper;

    public PreferencesService(UserRepository userRepository,
                              FavouriteCommuneRepository favouriteRepository,
                              WeatherAlertRepository alertRepository,
                              FavouriteMapper favouriteMapper) {
        this.userRepository = userRepository;
        this.favouriteRepository = favouriteRepository;
        this.alertRepository = alertRepository;
        this.favouriteMapper = favouriteMapper;
    }

    @Override
    @Transactional
    public void addFavourite(String userMail, String communeCode) {
        UserEntity user = userRepository.findByEmail(userMail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        List<FavouriteCommuneEntity> favorites = favouriteRepository.findAllByUser_Id(user.getId());
        boolean exists = favorites.stream()
                .anyMatch(fav -> fav.getCommuneCode().equals(communeCode));

        if(exists){
            throw new IllegalStateException("Cette commune est déjà dans vos favoris.");
        }
        FavouriteCommuneEntity favourite = new FavouriteCommuneEntity();
        favourite.setUser(user);
        favourite.setCommuneCode(communeCode);
        favouriteRepository.save(favourite);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FavouriteCommune> getFavouriteCommunes(String userMail) {
        UserEntity user = userRepository.findByEmail(userMail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        List<FavouriteCommuneEntity> entities = favouriteRepository.findAllByUser_Id(user.getId());

        return entities.stream().map(favouriteMapper::toDomain).toList();
    }

    @Override
    @Transactional
    public void removeFavourite(String userMail, String communeCode) {
        UserEntity user = userRepository.findByEmail(userMail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        favouriteRepository.deleteByUserAndCommuneCode(user.getId(), communeCode);

    }

    @Override
    @Transactional
    public void addAlert(String userMail, String communeCode, TypeAlert type, double threshold) {
        UserEntity user = userRepository.findByEmail(userMail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        WeatherAlertEntity alert = new WeatherAlertEntity();
        alert.setUser(user);
        alert.setCommuneCode(communeCode);
        alert.setTypeAlert(type);
        alert.setThresholdValue(threshold);
        alertRepository.save(alert);
    }

    @Override
    @Transactional
    public void updateAlert(String userMail, String communeCode, TypeAlert type, double threshold) {
        UserEntity user = userRepository.findByEmail(userMail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        WeatherAlertEntity alert = new WeatherAlertEntity();
        alert.setUser(user);
        alert.setCommuneCode(communeCode);
        alert.setTypeAlert(type);
        alert.setThresholdValue(threshold);
        alertRepository.save(alert);
    }

    @Override
    @Transactional
    public void silentAlert(String userEmail, String communeCode, TypeAlert type) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        WeatherAlertEntity alert = alertRepository.findByUserIdAndCommuneCodeAndTypeAlert(user.getId(), communeCode, type.toString());

        if(alert == null){
           throw new IllegalArgumentException("Aucune alerte ne correspond à la recherche");
        }
        alert.setActive(false);
        alertRepository.save(alert);

    }

    @Override
    @Transactional
    public void removeAlert(String userMail, String communeCode, TypeAlert type) {
        UserEntity user = userRepository.findByEmail(userMail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        WeatherAlertEntity alert = alertRepository.findByUserIdAndCommuneCodeAndTypeAlert(user.getId(), communeCode, type.toString());
        if(alert == null){
            throw new IllegalArgumentException("Aucune alerte ne correspond à la recherche");
        }
        alertRepository.delete(alert);
    }
}
