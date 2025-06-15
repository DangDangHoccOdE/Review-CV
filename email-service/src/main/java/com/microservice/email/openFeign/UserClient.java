package com.microservice.email.openfeign;

import com.microservice.email.dto.ApiResponse;
import com.microservice.email.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("user-service")
public interface UserClient {
    @GetMapping("/auth/findbyid")
    ApiResponse<UserDTO> findById(@RequestParam Integer id);
}
