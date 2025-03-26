package com.microservice.project.openFeign;

import com.microservice.project.dto.ApiResponse;
import com.microservice.project.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("user-service")
public interface UserClient {
    @GetMapping("/auth/getCurrentUser")
    ApiResponse<UserDto> getCurrentUser();
}
