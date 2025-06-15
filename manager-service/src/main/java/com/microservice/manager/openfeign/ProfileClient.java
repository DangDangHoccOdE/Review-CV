package com.microservice.manager.openfeign;

import com.microservice.manager.dto.ApiResponse;
import com.microservice.manager.dto.ProfileDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "profile-service")

public interface ProfileClient {
    @GetMapping("/profile/user/checkIdProfile")
    ApiResponse<String> checkIdProfile(@RequestParam Integer id);
    @GetMapping("/profile/user/findById")
    ApiResponse<ProfileDTO> getProfileById(@RequestParam Integer id) ;

}
