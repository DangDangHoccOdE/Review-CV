package com.microservice.manager.openfeign;

import com.microservice.manager.dto.ApiResponse;
import com.microservice.manager.dto.ImageDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient("image-service")
public interface ImageClient {
   @PostMapping(value = "/image/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
   ApiResponse<ImageDTO> save(@RequestPart(value="image",required = false)MultipartFile image) ;
}
