package com.microservice.project.openFeign;

import com.microservice.project.dto.ApiResponse;
import com.microservice.project.dto.ImageDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "image-service")
public interface ImageClient {
    @PostMapping(value = "/image/save", consumes = "multipart/form-data")
    ApiResponse<ImageDto> save(@RequestPart(value = "image", required = false)MultipartFile multipartFile);

    @GetMapping("/image/getAll")
    ApiResponse<String> getAll();
}
