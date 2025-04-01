package com.microservice.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private Integer id;
    private String message;
    private LocalDateTime createdAt;
    private String url;
    private boolean isRead;
    private Integer isUser;
}
