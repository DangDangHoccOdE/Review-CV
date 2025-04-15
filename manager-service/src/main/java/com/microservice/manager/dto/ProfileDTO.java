package com.microservice.manager.dto;

import com.microservice.manager.model.Contract;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {
    private Integer id;
    private String objective;
    private String education;
    private String workExperience;
    private String skills;
    private Contract contract;
    private String typeProfile;
    private Integer idUser;
    private String url;

    private String title;
}
