package com.microservice.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectDto {
    private Integer id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private String url;
    private String imageId;
    private boolean isDisplay;
    private MultipartFile imageFile;
    private Integer idProfile;
}
