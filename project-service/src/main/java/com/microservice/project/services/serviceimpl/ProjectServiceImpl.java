package com.microservice.project.services.serviceimpl;

import com.microservice.project.dto.ImageDTO;
import com.microservice.project.dto.ProfileDTO;
import com.microservice.project.dto.ProjectDTO;
import com.microservice.project.exception.CustomException;
import com.microservice.project.exception.Error;
import com.microservice.project.model.Project;
import com.microservice.project.openFeign.ImageClient;
import com.microservice.project.openFeign.ProfileClient;
import com.microservice.project.openFeign.Test;
import com.microservice.project.repository.ProjectRepository;
import com.microservice.project.services.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
    private Test ts;

    private ProjectDTO convertToDTO(Project project) {
        return modelMapper.map(project, ProjectDTO.class);
    }

    private Project convertToModel(ProjectDTO projectDTO) {
        return modelMapper.map(projectDTO, Project.class);
    }

    private List<ProjectDTO> convertToDTOList(List<Project> projects) {
        return projects.stream()
                .map(project -> modelMapper.map(project, ProjectDTO.class))
                .collect(Collectors.toList());
    }

    private Project save(ProjectDTO projectDTO) {
        try {
            log.info("Saving project");
            ImageDTO imageDTO = null;

            Project project = Project.builder()
                    .id(getGenerationId())
                    .title(projectDTO.getTitle())
                    .description(projectDTO.getDescription())
                    .idProfile(projectDTO.getIdProfile())
                    .url(projectDTO.getUrl())
                    .createAt(LocalDateTime.now())
                    .isDisplay(projectDTO.isDisplay())
                    .build();
            return projectRepository.save(project);
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(Error.PROJECT_UNABLE_TO_SAVE);
        } catch (DataAccessException e) {
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    public Integer getGenerationId() {
        UUID uuid = UUID.randomUUID();
        return (int) (uuid.getMostSignificantBits() & 0xFFFFFFFFL);
    }

    @Override
    public ProjectDTO saveProject(ProjectDTO projectDTO) {
        log.info("Save project");
        return convertToDTO(save(projectDTO));
    }

    @Override
    public ProjectDTO updateProject(ProjectDTO projectDTO) {
        try {
            log.info("Update project");
            projectDTO.setCreateAt(findById(projectDTO.getId()).getCreateAt());
            Project project = projectRepository.save(convertToModel(projectDTO));
            return convertToDTO(project);
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(Error.PROJECT_UNABLE_TO_UPDATE);
        } catch (DataAccessException e) {
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public ProjectDTO findById(Integer id) {
        log.info("Find project by id: {}", id);
        return convertToDTO(projectRepository.findById(id)
                .orElseThrow(() -> new CustomException(Error.PROJECT_NOT_FOUND)));
    }

    @Override
    public List<ProfileDTO> getAlliProfile() {
        try {
            log.info("Get all profile");
            return profileClient.getAll();
        } catch (DataAccessException e) {
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public List<ProjectDTO> getProjectByIdProfile(Integer id) {
        try {
            log.info("Get project by id profile: {}", id);
            return convertToDTOList(projectRepository.findByIdProfile(id));
        } catch (DataAccessException e) {
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public ImageDTO getall(MultipartFile multipartFile) {
        return imageClient.save(multipartFile).getData();
    }

    public boolean deleteProjectById(Integer id) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        if (optionalProject.isPresent()) {
            projectRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
