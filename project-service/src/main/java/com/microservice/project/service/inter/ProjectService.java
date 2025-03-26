package com.microservice.project.service.inter;

import com.microservice.project.dto.ImageDto;
import com.microservice.project.dto.ProfileDto;
import com.microservice.project.dto.ProjectDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProjectService {
    ProjectDto saveProject(ProjectDto projectDTO);
    ProjectDto updateProject(ProjectDto projectDTO);
    ProjectDto findById(Integer id);

    List<ProfileDto> getAlliProfile();
    List<ProjectDto>getProjectByIdProfile(Integer id);
    ImageDto getall(MultipartFile multipartFile);
}
