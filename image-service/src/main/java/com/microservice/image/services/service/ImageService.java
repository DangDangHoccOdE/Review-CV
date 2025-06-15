package com.microservice.image.services.service;

import com.microservice.image.dto.ImageDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface ImageService {

    ImageDTO saveImage(MultipartFile imageFile);

}