package com.microservice.image.controller;

import com.microservice.image.dto.ApiResponse;
import com.microservice.image.dto.ImageDTO;
import com.microservice.image.service.inter.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/image")
@RestController
@Slf4j
public class ImageController {
    @Autowired
    private ImageService imageService;

    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ImageDTO>> save(@RequestPart(value = "image", required = false)MultipartFile multipartFile){
        if(multipartFile == null || multipartFile.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        ImageDTO imageDTO = imageService.saveImage(multipartFile);
        log.info("image: {}", imageDTO);

        return ResponseEntity.ok(new ApiResponse<ImageDTO>(true,"Save image is successfully",imageDTO));
    }

    @GetMapping("getAll")
    public ResponseEntity<ApiResponse<String>> getAll() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Get all is successfully", "ok"));
    }
}
