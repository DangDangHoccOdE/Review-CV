package com.microservice.profile.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "profile")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Profile {
    @Id
    private Integer id;
    private String objective;
    private String education;
    private String workExperience;
    private String skills;
    private Contact contact;
    private TypeProfile typeProfile;
    private Integer idImage;
    private Integer idUser;
    private String url;
    private String title;
}
