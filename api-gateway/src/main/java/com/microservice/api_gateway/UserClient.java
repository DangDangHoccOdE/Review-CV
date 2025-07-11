package com.microservice.api_gateway;

import com.microservice.api_gateway.dto.ApiResponse;
import com.microservice.api_gateway.dto.AuthenticationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="user-service")
public interface UserClient {
    @PostMapping("/auth/isValid")
   ApiResponse<AuthenticationResponse> isValid(@RequestBody String token);
}
