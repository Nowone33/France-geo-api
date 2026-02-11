package fr.nowone.francegeoapi.infrastructure.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConfig {
    public static final String IMPORT_QUEUE = "import.queue";
    public static final String IMPORT_EXCHANGE = "import.exchange";
    public static final String IMPORT_ROUTING_KEY = "import.key";


    // 1. Définition de la queue
    @Bean
    public Queue importQueue() {
        return new Queue(IMPORT_QUEUE, true);
    }

    // 2. Définition de l'exchange
    @Bean
    public TopicExchange importExchange() {
        return new TopicExchange(IMPORT_EXCHANGE);
    }

    //3. Liaison (Binding) entre les deux
    @Bean
    public Binding binding(Queue importQueue, TopicExchange importExchange) {
        return BindingBuilder.bind(importQueue).to(importExchange).with(IMPORT_ROUTING_KEY);
    }

    //4 Important : Permet d'envoyer des objets Java DTO en Json automatiquement
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
