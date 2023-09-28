package com.sagatrading.config;

import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Bean
    public Declarables queues() {
        return new Declarables(
                new Queue("activities.queue",false),
                new Queue("marketOrderQueue",false),
                new Queue("productData.queueOp", false),
                new Queue("orderBook.queueOp", false),
                new Queue("orderProcessing.queue", false),
                new Queue("marketData.queueOp", false));
    }
}
