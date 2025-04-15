package com.microservice.manager.service.inter;

import com.microservice.manager.dto.JobDTO;
import com.microservice.manager.model.Job;

import java.util.List;

public interface JobService {
    JobDTO findById(Integer id);
    JobDTO create(JobDTO jobDTO);
    JobDTO update(JobDTO jobDTO);
    JobDTO delete(Integer id);
    List<JobDTO> findAll();
    List<JobDTO> getJobByCompany(Integer id);
    List<JobDTO> getNewJob(Integer id);
    List<JobDTO> getJobByProfilePending(Integer id);
    List<JobDTO> getJobByProfileAccepted(Integer id);
    JobDTO applyJob(Integer idJob, Integer idProfile);
    JobDTO acceptProfile(Integer idJob, Integer idProfile);
    JobDTO rejectProfile(Integer idJob, Integer idProfile);
}
