package com.microservice.profile.openFeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.microservice.profile.dto.ApiResponse;
import com.microservice.profile.dto.UserDTO;


@FeignClient("user-service")
public interface UserClient {
    @GetMapping("/checkId")
   Boolean checkId(@RequestParam Integer id);
   @GetMapping("/auth/getCurrentUser")
   ApiResponse<UserDTO>getCurrentUser() ;


}
