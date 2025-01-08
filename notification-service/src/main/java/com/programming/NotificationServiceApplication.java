package com.programming;

import com.programming.event.OrderPlacedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
@EnableDiscoveryClient
public class NotificationServiceApplication {
    Logger log = LoggerFactory.getLogger(NotificationServiceApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

    @KafkaListener(topics="notificationTopic")
    public void handleNotification(OrderPlacedEvent orderPlacedEvent) {
        log.info("Received Notification for Order -  {}", orderPlacedEvent.getOrderNumber());

    }

}
