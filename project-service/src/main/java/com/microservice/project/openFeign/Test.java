package com.microservice.project.openFeign;

import com.microservice.project.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("notification-service")
public interface  Test {
    @GetMapping(value = "/notification/getAll")
    ApiResponse<String> ok();
}
