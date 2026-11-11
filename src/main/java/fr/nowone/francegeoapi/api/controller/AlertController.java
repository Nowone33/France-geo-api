package fr.nowone.francegeoapi.api.controller;

import fr.nowone.francegeoapi.domain.model.TypeAlert;
import fr.nowone.francegeoapi.domain.ports.primary.PreferencesPrimaryPort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final PreferencesPrimaryPort preferencesPort;

    public  AlertController(PreferencesPrimaryPort preferencesPort) {
        this.preferencesPort = preferencesPort;
    }
    public record AlertRequest(String communeCode, String typeAlert, double thresholdValue) {}

    @PostMapping
    public ResponseEntity<Void> addAlert(
            @RequestBody AlertRequest request,
            @AuthenticationPrincipal UserDetails user){

        preferencesPort.addAlert(user.getUsername(), request.communeCode(), TypeAlert.valueOf(request.typeAlert()), request.thresholdValue());
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<?> updateAlertOnCommune(
            @RequestBody AlertRequest request,
            @AuthenticationPrincipal UserDetails user){

        preferencesPort.updateAlert(user.getUsername(), request.communeCode(), TypeAlert.valueOf(request.typeAlert()), request.thresholdValue());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{communeCode}/{typeAlert}/silent") //  PATCH pour modification partielle lors de la desactivation
    public ResponseEntity<Void> silentAlert(
            @PathVariable String communeCode,
            @PathVariable String typeAlert,
            @AuthenticationPrincipal UserDetails user) {
        preferencesPort.silentAlert(user.getUsername(), communeCode, TypeAlert.valueOf(typeAlert));
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{communeCode}/{typeAlert}")
    public ResponseEntity<Void> removeAlert(
            @PathVariable String communeCode,
            @PathVariable String typeAlert,
            @AuthenticationPrincipal UserDetails user) {
        preferencesPort.removeAlert(user.getUsername(), communeCode, TypeAlert.valueOf(typeAlert));
        return ResponseEntity.noContent().build();
    }

}
