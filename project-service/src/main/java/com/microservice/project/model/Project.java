package com.microservice.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "project")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Project {
    @Id
    private Integer id;
    private String title;
    private String description;
    private LocalDateTime createAt;
    private String idImage;
    private boolean isDisplay;
    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();

    }
    private String url;
    private Integer idProfile;

}

