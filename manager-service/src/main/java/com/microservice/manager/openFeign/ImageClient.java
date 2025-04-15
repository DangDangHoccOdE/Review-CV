package com.microservice.manager.openFeign;

import com.microservice.manager.dto.ApiResponse;
import com.microservice.manager.dto.ImageDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient("image-service")
public interface ImageClient {
    @PostMapping("/image/save")
    ApiResponse<ImageDTO> save(@RequestPart(value = "image",required = false)MultipartFile multipartFile);
}
