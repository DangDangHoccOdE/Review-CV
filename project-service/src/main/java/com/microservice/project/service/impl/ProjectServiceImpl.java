package com.microservice.project.service.impl;

import com.microservice.project.dto.ImageDto;
import com.microservice.project.dto.ProfileDto;
import com.microservice.project.dto.ProjectDto;
import com.microservice.project.exception.CustomException;
import com.microservice.project.exception.Error;
import com.microservice.project.model.Project;
import com.microservice.project.openFeign.ImageClient;
import com.microservice.project.openFeign.ProfileClient;
import com.microservice.project.openFeign.test;
import com.microservice.project.repository.ProjectRepository;
import com.microservice.project.service.inter.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProjectServiceImpl implements ProjectService {
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProfileClient profileClient;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ImageClient imageClient;
    @Autowired
    private test ts;

    private ProjectDto convertToDTO(Project project) {
        return modelMapper.map(project, ProjectDto.class);
    }

    private Project convertToModel(ProjectDto projectDTO) {
        return modelMapper.map(projectDTO, Project.class);
    }

    private List<ProjectDto> convertToDTOList(List<Project> projects) {
        return projects.stream()
                .map(project -> modelMapper.map(project, ProjectDto.class))
                .collect(Collectors.toList());
    }

    public Integer getGenerationId() {
        UUID uuid = UUID.randomUUID();
        return (int) (uuid.getMostSignificantBits() & 0xFFFFFFFFL);
    }

    @Override
    public ProjectDto saveProject(ProjectDto projectDTO) {
        log.info("Save project");
        return convertToDTO(save(projectDTO));
    }

    private Project save(ProjectDto projectDTO) {
        try {
            log.info("Saving project");
            ImageDto imageDTO = null;

            Project project = Project.builder()
                    .id(getGenerationId())
                    .title(projectDTO.getTitle())
                    .description(projectDTO.getDescription())
                    .idProfile(projectDTO.getIdProfile())
                    .url(projectDTO.getUrl())
                    .createdAt(LocalDateTime.now())
                    .isDisplay(projectDTO.isDisplay())
                    .build();
            return projectRepository.save(project);
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(Error.PROJECT_UNABLE_TO_SAVE);
        } catch (DataAccessException e) {
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public ProjectDto updateProject(ProjectDto projectDTO) {
        try {
            log.info("Update project");
            projectDTO.setCreatedAt(findById(projectDTO.getId()).getCreatedAt());
            Project project = projectRepository.save(convertToModel(projectDTO));
            return convertToDTO(project);
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(Error.PROJECT_UNABLE_TO_UPDATE);
        } catch (DataAccessException e) {
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public ProjectDto findById(Integer id) {
        log.info("Find project by id: {}", id);
        return convertToDTO(projectRepository.findById(id)
                .orElseThrow(() -> new CustomException(Error.PROJECT_NOT_FOUND)));
    }

    @Override
    public List<ProfileDto> getAlliProfile() {
        try {
            log.info("Get all profile");
            return profileClient.getAll();
        } catch (DataAccessException e) {
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public List<ProjectDto> getProjectByIdProfile(Integer id) {
        try {
            log.info("Get project by id profile: {}", id);
            return convertToDTOList(projectRepository.findByIdProfile(id));
        } catch (DataAccessException e) {
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public ImageDto getall(MultipartFile multipartFile) {
        return imageClient.save(multipartFile).getData();
    }
}
