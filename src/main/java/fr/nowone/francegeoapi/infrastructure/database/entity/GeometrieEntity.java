package fr.nowone.francegeoapi.infrastructure.database.entity;

public class GeometrieEntity {
    private String type;
    private Object coordinates;

    public GeometrieEntity() {}

    public GeometrieEntity(String type, Object coordinates) {
        this.type = type;
        this.coordinates = coordinates;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Object coordinate) {
        this.coordinates = coordinates;
    }
}
