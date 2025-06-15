package com.microservice.image.services.serviceimpl;

import com.microservice.image.dto.ImageDTO;
import com.microservice.image.model.Image;
import com.microservice.image.repository.ImageRepository;
import com.microservice.image.services.CloudinaryService;
import com.microservice.image.services.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
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

    private Image save(MultipartFile imageFile) {
        log.info("Uploading image");
        Map<String, Object> resultMap = cloudinaryService.upload(imageFile);
        String imageUrl = (String) resultMap.get("url");
        Image image = Image.builder()
                .url(imageUrl)
                .id(getGenerationId())
                .build();
        return imageRepository.save(image);
    }

    @Override
    public ImageDTO saveImage(MultipartFile imageFile) {
         Image image=save(imageFile);
        return ImageDTO.builder().id(image.getId()).url(image.getUrl()).build();

    }

    public Integer getGenerationId() {
        UUID uuid = UUID.randomUUID();
        return (int) (uuid.getMostSignificantBits() & 0xFFFFFFFFL);
    }

    public ImageDTO coventToDTO(Image image) {
        return modelMapper.map(image, ImageDTO.class);
    }
}
