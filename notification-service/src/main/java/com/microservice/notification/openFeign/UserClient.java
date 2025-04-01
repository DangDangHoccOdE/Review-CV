package com.microservice.notification.openFeign;

import com.microservice.notification.dto.ApiResponse;
import com.microservice.notification.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
@FeignClient(name = "user-service")
public interface UserClient {
    @GetMapping("/auth/checkId")
    ApiResponse<Boolean> checkId(@RequestParam Integer id);
    @GetMapping("/auth/getCurrentUser")
    ApiResponse<UserDTO> getCurrentUser() ;
}
