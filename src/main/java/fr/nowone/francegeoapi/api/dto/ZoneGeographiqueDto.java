package fr.nowone.francegeoapi.api.dto;



public record ZoneGeographiqueDto (String id,
                                   String code,
                                   String nom,
                                   String type,
                                   String parentCode,
                                   GeometrieDto geometrie,
                                   Long population) {
}

