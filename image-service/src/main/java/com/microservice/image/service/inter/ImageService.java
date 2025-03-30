package com.microservice.image.service.inter;

import com.microservice.image.dto.ImageDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    ImageDTO saveImage(MultipartFile file);
}
