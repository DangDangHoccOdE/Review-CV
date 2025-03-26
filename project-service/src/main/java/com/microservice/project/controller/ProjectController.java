package com.microservice.project.controller;

import com.microservice.project.dto.ApiResponse;
import com.microservice.project.dto.ImageDto;
import com.microservice.project.dto.ProfileDto;
import com.microservice.project.dto.ProjectDto;
import com.microservice.project.openFeign.ImageClient;
import com.microservice.project.service.inter.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("project")
public class ProjectController {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ImageClient imageClient;

    @PostMapping("/user/save")
    public ResponseEntity<ApiResponse<ProjectDto>> save(@RequestBody ProjectDto projectDTO) {
        ProjectDto savedProject = projectService.saveProject(projectDTO);
        ApiResponse<ProjectDto> response = new ApiResponse<>(true, "Project saved successfully", savedProject);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/user/update")
    public ResponseEntity<ApiResponse<ProjectDto>> update(@RequestBody ProjectDto projectDTO) {
        ProjectDto updatedProject = projectService.updateProject(projectDTO);
        ApiResponse<ProjectDto> response = new ApiResponse<>(true, "Project updated successfully", updatedProject);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/getProfile")
    public ResponseEntity<ApiResponse<List<ProfileDto>>> getProfile() {
        List<ProfileDto> profiles = projectService.getAlliProfile();
        ApiResponse<List<ProfileDto>> response = new ApiResponse<>(true, "Profiles fetched successfully", profiles);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/getProject")
    public ResponseEntity<ApiResponse<List<ProjectDto>>> getProjectByIdProfile(@RequestParam Integer id) {
        List<ProjectDto> projects = projectService.getProjectByIdProfile(id);
        ApiResponse<List<ProjectDto>> response = new ApiResponse<>(true, "Projects fetched successfully", projects);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/get")
    public ResponseEntity<ApiResponse<ImageDto>> get(@RequestPart(value="image",required = false) MultipartFile image) {
        ApiResponse<ImageDto> response = new ApiResponse<>(true, "ok", projectService.getall(image));
        return ResponseEntity.ok(response);
    }
    @GetMapping("/user/get1")
    public ResponseEntity<ApiResponse<String>> get1() {
        ApiResponse<String> response = new ApiResponse<>(true, "ok", imageClient.getAll().getData());
        return ResponseEntity.ok(response);
    }
}
