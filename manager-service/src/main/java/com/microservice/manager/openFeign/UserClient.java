package com.microservice.manager.openFeign;

import com.microservice.manager.dto.ApiResponse;
import com.microservice.manager.dto.AuthenticationRequest;
import com.microservice.manager.dto.AuthenticationResponse;
import com.microservice.manager.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("user-service")
public interface UserClient {
    @PostMapping("/auth/signup")
    ApiResponse<AuthenticationResponse> signUp(@RequestBody AuthenticationRequest authenticationRequest);

    @GetMapping("/auth/getCurrentUser")
    ApiResponse<UserDTO> getCurrentUser();

    @GetMapping("/findById")
    ApiResponse<UserDTO> findById(@RequestParam("id") Integer id);
}
