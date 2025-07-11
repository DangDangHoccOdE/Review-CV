package com.microservice.project.openFeign;

import com.microservice.project.dto.ProfileDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "profile-service")
public interface ProfileClient {
    @GetMapping("/user/profile/getAll")
   List< ProfileDTO> getAll() ;

}
