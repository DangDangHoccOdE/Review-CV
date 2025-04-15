package com.microservice.manager.openFeign;

import com.microservice.manager.dto.ApiResponse;
import com.microservice.manager.dto.ProfileDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("profile-service")
public interface ProfileClient {
    @GetMapping("/profile/user/checkIdProfile")
    ApiResponse<String> checkIdProfile(@RequestParam("id") Integer id);

    @GetMapping("/profile/user/findById")
    ApiResponse<ProfileDTO> getProfileById(@RequestParam("id") Integer id);
}
