package com.microservice.profile.openFeign;

import com.microservice.profile.config.FeignClientConfig;
import com.microservice.profile.dto.ApiResponse;
import com.microservice.profile.dto.ImageDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "image-service", configuration = FeignClientConfig.class)

public interface ImageClient {
    @PostMapping(value = "/image/save", consumes = "multipart/form-data")
    ApiResponse<ImageDTO> save(@RequestPart(value="image",required = false)MultipartFile image) ;

    @GetMapping("/image/getAll")
    ApiResponse<String> getAll();
}
