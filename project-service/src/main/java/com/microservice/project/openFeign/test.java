package com.microservice.project.openFeign;

import com.microservice.project.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("notification-service")
public interface test {
    @GetMapping(value = "/notification/getAll")
    ApiResponse<String> ok();
}
