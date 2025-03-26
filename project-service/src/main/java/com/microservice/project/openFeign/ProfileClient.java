package com.microservice.project.openFeign;

import com.microservice.project.dto.ProfileDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "profile-service")
public interface ProfileClient {
    @GetMapping("/user/profile/getAll")
    List<ProfileDto> getAll();
}
