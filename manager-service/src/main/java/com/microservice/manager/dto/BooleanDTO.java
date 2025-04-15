package com.microservice.manager.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BooleanDTO {
    private boolean isCheck;
}
