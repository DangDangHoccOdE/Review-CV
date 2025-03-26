package com.microservice.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileDto {
    private Integer id;
    private String objective;
    private String education;
    private String workExperience;
    private String skills;
    private String typeProfile;
    private Integer userId;
    private String url;
    private MultipartFile imageFile;
}
