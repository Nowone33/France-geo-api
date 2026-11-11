package fr.nowone.francegeoapi.api.controller;

import fr.nowone.francegeoapi.domain.model.FavouriteCommune;
import fr.nowone.francegeoapi.domain.ports.primary.PreferencesPrimaryPort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favourites")
public class FavouriteController {

    private final PreferencesPrimaryPort preferencesPort;

    public FavouriteController(PreferencesPrimaryPort preferencesPort) {
        this.preferencesPort = preferencesPort;
    }


    @PostMapping("/{communeCode}")
    public ResponseEntity<Void> addFavourite(
            @PathVariable String communeCode,
            @AuthenticationPrincipal UserDetails user){

        preferencesPort.addFavourite(user.getUsername(), communeCode);
        return ResponseEntity.ok().build();
    }

    @GetMapping()
    public ResponseEntity<List<FavouriteCommune>> getFavourites(@AuthenticationPrincipal UserDetails user){
        return ResponseEntity.ok(preferencesPort.getFavouriteCommunes(user.getUsername()));

    }

    @DeleteMapping("/{communeCode")
    public ResponseEntity<Void> deleteFavourite(
            @PathVariable String codeCommune,
            @AuthenticationPrincipal UserDetails user){

        preferencesPort.removeFavourite(user.getUsername(), codeCommune);
        return ResponseEntity.noContent().build(); //204
    }
}
