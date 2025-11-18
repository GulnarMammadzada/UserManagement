package com.example.usermanagement.kafka;

import com.example.usermanagement.dto.UserEventDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.topic.user-events}", groupId = "user-management-group")
    public void consumeUserEvent(String message) {
        try {
            UserEventDTO event = objectMapper.readValue(message, UserEventDTO.class);
            log.info("Consumed user event: type={}, userId={}, email={}",
                    event.getEventType(), event.getUserId(), event.getEmail());

            processEvent(event);
        } catch (Exception e) {
            log.error("Error processing user event", e);
        }
    }

    private void processEvent(UserEventDTO event) {
        switch (event.getEventType()) {
            case "USER_CREATED":
                log.info("Processing USER_CREATED event for user: {}", event.getUserId());
                break;
            case "USER_UPDATED":
                log.info("Processing USER_UPDATED event for user: {}", event.getUserId());
                break;
            case "USER_DELETED":
                log.info("Processing USER_DELETED event for user: {}", event.getUserId());
                break;
            case "USER_STATUS_CHANGED":
                log.info("Processing USER_STATUS_CHANGED event for user: {}", event.getUserId());
                break;
            default:
                log.warn("Unknown event type: {}", event.getEventType());
        }
    }
}
