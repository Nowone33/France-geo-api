package fr.nowone.francegeoapi.infrastructure.database.sql.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String name;


    /*
     ***********         Constructor + Getters & Setters *************************************
     */

    public RoleEntity() {
    }

    public RoleEntity(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
