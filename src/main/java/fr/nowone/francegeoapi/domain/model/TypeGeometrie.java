package fr.nowone.francegeoapi.domain.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TypeGeometrie {
    POINT("Point"),
    POLYGON("Polygon"),
    MULTI_POLYGON("MultiPolygon");

    private final String value;

    TypeGeometrie(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TypeGeometrie fromValue(String text) {
        for (TypeGeometrie type : TypeGeometrie.values()) {
            if (type.value.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Type de géométrie inconnu : " + text);
    }

}
