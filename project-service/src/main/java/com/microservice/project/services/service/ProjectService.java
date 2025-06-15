package com.microservice.project.services.service;

import com.microservice.project.dto.ImageDTO;
import com.microservice.project.dto.ProfileDTO;
import com.microservice.project.dto.ProjectDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProjectService {
    ProjectDTO saveProject(ProjectDTO projectDTO);
    ProjectDTO updateProject(ProjectDTO projectDTO);
    ProjectDTO findById(Integer id);

    List<ProfileDTO>getAlliProfile();
    List<ProjectDTO>getProjectByIdProfile(Integer id);
    ImageDTO getall(MultipartFile multipartFile);
    boolean deleteProjectById(Integer id);
}
