package com.microservice.manager.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "job")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Job {
    @Id
    private Integer id;
    private String title;
    private String description;
    private TypeJob typeJob;
    private Integer size;
    private List<Integer> idProfilePending;
    private List<Integer> idProfile;
    private Integer idCompany;
}
