package fr.nowone.francegeoapi.domain.ports.primary;

import fr.nowone.francegeoapi.domain.model.FavouriteCommune;
import fr.nowone.francegeoapi.domain.model.TypeAlert;

import java.util.List;

public interface PreferencesPrimaryPort {

    void addFavourite(String userMail, String communeCode);
    List<FavouriteCommune> getFavouriteCommunes(String userMail);
    void removeFavourite(String userMail, String communeCode);

    void addAlert(String userMail, String communeCode, TypeAlert type, double threshold);
    void updateAlert(String userMail, String communeCode, TypeAlert type, double threshold);
    void silentAlert(String userMail, String communeCode, TypeAlert type);
    void removeAlert(String userMail, String communeCode, TypeAlert type);
}
