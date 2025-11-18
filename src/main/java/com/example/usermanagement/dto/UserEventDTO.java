package com.example.usermanagement.dto;

import com.example.usermanagement.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEventDTO {

    private String eventType;
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private User.UserRole role;
    private User.UserStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Builder.Default
    private LocalDateTime eventTimestamp = LocalDateTime.now();

    private String performedBy;

    public enum EventType {
        USER_CREATED,
        USER_UPDATED,
        USER_DELETED,
        USER_STATUS_CHANGED
    }
}
