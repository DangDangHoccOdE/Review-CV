package com.microservice.image.service.impl;

import com.microservice.image.dto.ImageDTO;
import com.microservice.image.model.Image;
import com.microservice.image.repository.ImageRepository;
import com.microservice.image.service.CloudinaryService;
import com.microservice.image.service.inter.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class ImageServiceImpl implements ImageService {
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private ModelMapper modelMapper;

    public Integer getGenerationId() {
        UUID uuid = UUID.randomUUID();
        return (int) (uuid.getMostSignificantBits() & 0xFFFFFFFFL);
    }

    private Image save(MultipartFile multipartFile){
        log.info("Uploading image");
        Map<String, Object> resultMap = cloudinaryService.upload(multipartFile);
        String imageUrl = (String)  resultMap.get("url");
        Image image = Image.builder()
                .url(imageUrl)
                .id(getGenerationId())
                .build();
        return imageRepository.save(image);
    }

    @Override
    public ImageDTO saveImage(MultipartFile file) {
        Image image = save(file);
        return modelMapper.map(image, ImageDTO.class);
    }

    public ImageDTO convertToDTO(Image image) {
        return modelMapper.map(image, ImageDTO.class);
    }
}
