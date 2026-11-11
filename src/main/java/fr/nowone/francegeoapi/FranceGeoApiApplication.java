package fr.nowone.francegeoapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "fr.nowone.francegeoapi.infrastructure.database.sql.repository")
@EnableMongoRepositories(basePackages = "fr.nowone.francegeoapi.infrastructure.database.mongo.repository")
public class FranceGeoApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FranceGeoApiApplication.class, args);
    }

}
