package fr.nowone.francegeoapi.infrastructure.database.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;


@Document(collection = "zones")
public class ZoneGeographiqueEntity {
    @Id
    private String id;
    @Indexed
    private String code;
    private String nom;
    private String type;
    @Indexed
    private String parentCode;
    private Map<String, Object> geometrie;
    private Long population;




    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }


    public Map<String, Object> getGeometrie() {
        return geometrie;
    }

    public void setGeometrie(Map<String, Object> geometrie) {
        this.geometrie = geometrie;
    }

    public Long getPopulation() {
        return population;
    }

    public void setPopulation(Long population) {
        this.population = population;
    }
}
