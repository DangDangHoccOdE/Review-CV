package com.microservice.api_gateway;

import com.microservice.api_gateway.dto.ApiResponse;
import com.microservice.api_gateway.dto.AuthenticationRequest;
import com.microservice.api_gateway.dto.AuthenticationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;
@FeignClient(name="user-service")
public interface UserClient {
    @PostMapping("/auth/isValid")
   ApiResponse<AuthenticationResponse> isValid(@RequestBody String token);
}
