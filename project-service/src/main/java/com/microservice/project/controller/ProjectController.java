package com.microservice.project.controller;

import com.microservice.project.dto.ApiResponse;
import com.microservice.project.dto.ImageDTO;
import com.microservice.project.dto.ProfileDTO;
import com.microservice.project.dto.ProjectDTO;
import com.microservice.project.openFeign.ImageClient;
import com.microservice.project.services.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse<ProjectDTO>> save(@RequestBody ProjectDTO projectDTO) {
        ProjectDTO savedProject = projectService.saveProject(projectDTO);
        ApiResponse<ProjectDTO> response = new ApiResponse<>(true, "Project saved successfully", savedProject);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/user/update")
    public ResponseEntity<ApiResponse<ProjectDTO>> update(@RequestBody ProjectDTO projectDTO) {
        ProjectDTO updatedProject = projectService.updateProject(projectDTO);
        ApiResponse<ProjectDTO> response = new ApiResponse<>(true, "Project updated successfully", updatedProject);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/getProfile")
    public ResponseEntity<ApiResponse<List<ProfileDTO>>> getProfile() {
        List<ProfileDTO> profiles = projectService.getAlliProfile();
        ApiResponse<List<ProfileDTO>> response = new ApiResponse<>(true, "Profiles fetched successfully", profiles);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/getProject")
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getProjectByIdProfile(@RequestParam Integer id) {
        List<ProjectDTO> projects = projectService.getProjectByIdProfile(id);
        ApiResponse<List<ProjectDTO>> response = new ApiResponse<>(true, "Projects fetched successfully", projects);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/get")
    public ResponseEntity<ApiResponse<ImageDTO>> get(@RequestPart(value="image",required = false)MultipartFile image) {
        ApiResponse<ImageDTO> response = new ApiResponse<>(true, "ok", projectService.getall(image));
        return ResponseEntity.ok(response);
    }
    @GetMapping("/user/get1")
    public ResponseEntity<ApiResponse<String>> get1() {
        ApiResponse<String> response = new ApiResponse<>(true, "ok", imageClient.getAll().getData());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/user/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProjectByUser(@PathVariable Integer id) {
        try {
            boolean deleted = projectService.deleteProjectById(id); // Giả sử bạn có service như vậy
            if (deleted) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Xoá thành công", "Project đã được xoá"));
            } else {
                return ResponseEntity.ok(new ApiResponse<>(false, "Không tìm thấy project", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Lỗi server: " + e.getMessage(), null));
        }
    }
}
