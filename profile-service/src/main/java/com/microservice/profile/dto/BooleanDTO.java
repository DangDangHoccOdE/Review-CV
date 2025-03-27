package com.microservice.profile.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BooleanDTO {
    private boolean isCheck;
}
