package com.microservice.profile.openFeign;

import com.microservice.profile.dto.ApiResponse;
import com.microservice.profile.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("user-service")
public interface UserClient {
    @GetMapping("/checkId")
    Boolean checkId(@RequestParam("id") Integer id) ;
    @GetMapping("/auth/getCurrentUser")
    ApiResponse<UserDTO> getCurrentUser();
}
