package com.microservice.notification.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageDTO {
    private  String message;
    private  Integer id;
}
