package com.example.usermanagement.kafka;

import com.example.usermanagement.dto.UserEventDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.user-events}")
    private String userEventsTopic;

    public void sendUserEvent(UserEventDTO event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            CompletableFuture<SendResult<String, String>> future =
                    kafkaTemplate.send(userEventsTopic, event.getUserId().toString(), eventJson);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("User event sent successfully: {} for user: {}",
                            event.getEventType(), event.getUserId());
                } else {
                    log.error("Failed to send user event: {} for user: {}",
                            event.getEventType(), event.getUserId(), ex);
                }
            });
        } catch (JsonProcessingException e) {
            log.error("Error serializing user event", e);
        }
    }
}
