package com.microservice.manager.openfeign;

import com.microservice.manager.dto.ApiResponse;
import com.microservice.manager.dto.AuthenticationRequest;
import com.microservice.manager.dto.AuthenticationResponse;
import com.microservice.manager.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name ="user-service" )
public interface UserClient {
    @PostMapping("/auth/signup")
    ApiResponse<AuthenticationResponse> signUp(@RequestBody AuthenticationRequest signUpRequest);

    @GetMapping("/auth/getCurrentUser")
    ApiResponse<UserDTO> getCurrentUser();

    @GetMapping("/findbyid")
    ApiResponse<UserDTO> findById(@RequestParam Integer id);
}
