package com.microservice.notification.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MessageDTO {
    private String message;
    private Integer id;
}
