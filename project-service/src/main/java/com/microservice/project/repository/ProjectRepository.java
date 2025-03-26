package com.microservice.project.repository;

import com.microservice.project.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project,Integer> {
    List<Project> findByIdProfile(Integer idProfile);
}
