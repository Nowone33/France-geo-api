package fr.nowone.francegeoapi.infrastructure.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        //Augmentation de la taille mémoire pour les gros GeoJson
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> {
                            codecs.defaultCodecs().maxInMemorySize(16 * 1024*1024);
                            codecs.defaultCodecs().jackson2JsonDecoder(
                                    new Jackson2JsonDecoder(new ObjectMapper(), MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON));
                        })
                .build();

        return builder
                .exchangeStrategies(strategies)
                .build();
    }
}
