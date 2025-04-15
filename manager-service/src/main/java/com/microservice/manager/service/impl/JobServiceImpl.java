package com.microservice.manager.service.impl;

import com.microservice.manager.dto.JobDTO;
import com.microservice.manager.dto.MessageDTO;
import com.microservice.manager.dto.ProfileDTO;
import com.microservice.manager.exception.CustomException;
import com.microservice.manager.exception.Error;
import com.microservice.manager.model.Job;
import com.microservice.manager.model.TypeJob;
import com.microservice.manager.openFeign.EmailClient;
import com.microservice.manager.openFeign.NotificationClient;
import com.microservice.manager.openFeign.ProfileClient;
import com.microservice.manager.repository.JobRepository;
import com.microservice.manager.service.inter.JobService;
import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoCommandException;
import com.mongodb.MongoWriteException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JobServiceImpl implements JobService {
    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ProfileClient profileClient;

    @Autowired
    private NotificationClient notificationClient;

    @Autowired
    private EmailClient emailClient;

    private Integer getGenerationId() {
        UUID uuid = UUID.randomUUID();
        return (int) (uuid.getMostSignificantBits() & 0x7FFFFFFF);
    }

    private Job convertToModel(JobDTO jobDTO) {
        return modelMapper.map(jobDTO, Job.class);
    }

    private JobDTO convertToDto(Job job) {
        return modelMapper.map(job, JobDTO.class);
    }

    private List<JobDTO> convertToDtoList(List<Job> jobs) {
        return jobs.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private Job save(JobDTO jobDTO) {
        try{
            log.info("Inserting job");
            Job job = Job.builder()
                    .id(getGenerationId())
                    .typeJob(TypeJob.valueOf(jobDTO.getTypeJob()))
                    .title(jobDTO.getTitle())
                    .description(jobDTO.getDescription())
                    .size(jobDTO.getSize())
                    .idProfilePending(jobDTO.getIdProfilePending())
                    .idCompany(jobDTO.getIdCompany())
                    .build();
            return jobRepository.insert(job);
        }catch (DuplicateKeyException e){
            throw new CustomException(Error.MONGO_DUPLICATE_KEY_ERROR);
        }catch (DataAccessException e){
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public JobDTO findById(Integer id) {
        log.info("Finding job by id: {}",id);
        return convertToDto(jobRepository.findById(id)
                .orElseThrow(()-> new CustomException(Error.JOB_NOT_FOUND)));
    }

    @Override
    public JobDTO create(JobDTO jobDTO) {
        log.info("Creating job");
        return convertToDto(save(jobDTO));
    }

    @Override
    public JobDTO update(JobDTO jobDTO) {
        try{
            log.info("Updating job by id: {}",jobDTO.getId());
            return convertToDto(jobRepository.save(convertToModel(jobDTO)));
        }catch (MongoWriteException e){
            throw new CustomException(Error.MONGO_WRITE_CONCERN_ERROR);
        }catch (DataAccessException ignored){}
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
    }

    @Override
    public JobDTO delete(Integer id) {
        try{
            log.info("Deleting job by id: {}",id);
            Job job = jobRepository.findById(id).orElseThrow(()-> new CustomException(Error.JOB_NOT_FOUND));
            jobRepository.delete(job);
            return convertToDto(job);
        }catch (DataAccessException ignored){
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public List<JobDTO> getJobByCompany(Integer id) {
        return List.of();
    }

    @Override
    public List<JobDTO> findAll() {
        return List.of();
    }

    @Override
    public List<JobDTO> getNewJob(Integer id) {
        try{
            log.info("Fetching new jobs for profile: {}", id);
            Query query = new Query();
            query.addCriteria(Criteria.where("idProfilePending").nin(id)
                    .and("idProfile").nin(id));
            return convertToDtoList(mongoTemplate.find(query, Job.class));
        }catch (MongoCommandException e){
            throw new CustomException(Error.MONGO_QUERY_EXECUTION_ERROR);
        }catch (DataAccessException e){
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public List<JobDTO> getJobByProfilePending(Integer id) {
        try{
            log.info("Fetching jobs for profile pending id:: {}", id);
            Query query = new Query();
            query.addCriteria(Criteria.where("idProfilePending").is(id));
            return convertToDtoList(mongoTemplate.find(query, Job.class));
        }catch (MongoCommandException e){
            throw new CustomException(Error.MONGO_QUERY_EXECUTION_ERROR);
        }catch (DataAccessException e){
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public JobDTO applyJob(Integer idJob, Integer idProfile) {
        return null;
    }

    @Override
    public List<JobDTO> getJobByProfileAccepted(Integer id) {
        try{
            log.info("Fetching jobs by profile accepted id:: {}", id);
            Query query = new Query();
            query.addCriteria(Criteria.where("idProfile").is(id));
            return convertToDtoList(mongoTemplate.find(query, Job.class));
        }catch (MongoCommandException e){
            throw new CustomException(Error.MONGO_QUERY_EXECUTION_ERROR);
        }catch (DataAccessException e){
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public JobDTO acceptProfile(Integer idJob, Integer idProfile) {
        try{
            log.info("Accepting profile id: {}. job id: {}", idProfile, idJob);
            JobDTO jobDTO = findById(idJob);
            if (jobDTO.getIdProfile() == null){
                jobDTO.setIdProfile(new ArrayList<>());
            }

            if(jobDTO.getIdProfilePending() == null){
                jobDTO.setIdProfilePending(new ArrayList<>());
            }
            List<Integer> idProfileJob = jobDTO.getIdProfile();
            int size = jobDTO.getSize()-1;
            jobDTO.setSize(size);
            idProfileJob.add(idProfile);
            jobDTO.setIdProfile(idProfileJob);
            JobDTO jobAccept = update(jobDTO);
            ProfileDTO profileDTO = profileClient.getProfileById(idProfile).getData();
            MessageDTO messageDTO = MessageDTO.builder()
                    .message("accept job successful by" + jobDTO.getTypeJob())
                    .id(profileDTO.getIdUser()).build();
            JobDTO jobDTO1 = update(jobDTO);
            notificationClient.create(messageDTO);
            emailClient.send(messageDTO);
            return jobDTO1;
        }catch (DataIntegrityViolationException e){
            throw new CustomException(Error.COMPANY_UNABLE_TO_UPDATE);
        }catch (DataAccessException e){
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public JobDTO rejectProfile(Integer idJob, Integer idProfile) {
        try{
            log.info("Rejecting profile id: {}. job id: {}", idProfile, idJob);
            JobDTO jobDTO = findById(idJob);
            List<Integer> idProfilePending = jobDTO.getIdProfilePending();
            idProfilePending.remove(idProfile);
            jobDTO.setIdProfilePending(idProfilePending);
            return update(jobDTO);
        }catch (DataIntegrityViolationException e){
            throw new CustomException(Error.COMPANY_UNABLE_TO_UPDATE);
        }catch (DataAccessException e){
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }
}
