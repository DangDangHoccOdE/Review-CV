package com.microservice.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobDTO {
    private Integer id;
    private String title;
    private String description;
    private String typeJob;
    private Integer size;
    private List<Integer> idProfilePending;
    private List<Integer> idProfile;
    private Integer idCompany;
}
