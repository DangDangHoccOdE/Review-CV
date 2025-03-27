package com.microservice.profile.service.impl;

import com.microservice.profile.ProfileRepository;
import com.microservice.profile.dto.ApiResponse;
import com.microservice.profile.dto.BooleanDTO;
import com.microservice.profile.dto.ImageDTO;
import com.microservice.profile.dto.ProfileDTO;
import com.microservice.profile.exception.CustomException;
import com.microservice.profile.exception.Error;
import com.microservice.profile.model.Profile;
import com.microservice.profile.model.TypeProfile;
import com.microservice.profile.openFeign.ImageClient;
import com.microservice.profile.openFeign.UserClient;
import com.microservice.profile.service.inter.ProfileService;
import com.mongodb.MongoCommandException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.StringOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.mongodb.core.query.Query;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProfileServiceImpl implements ProfileService {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ImageClient imageClient;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserClient userClient;

    public List<ProfileDTO> convertToDTOList(List<Profile> profiles) {
        return profiles.stream()
                .map(profile -> modelMapper.map(profile, ProfileDTO.class))
                .collect(Collectors.toList());
    }

//    public ProfileDTO convertToDTO(Profile profile) {
//        return modelMapper.map(profile, ProfileDTO.class);
//    }

    public Profile convertToModel(ProfileDTO dto) {
        return modelMapper.map(dto, Profile.class);
    }

    public Integer getGenerationId() {
        UUID uuid = UUID.randomUUID();
        return (int) (uuid.getMostSignificantBits() & 0xFFFFFFFFL);
    }

    private Profile save(ProfileDTO dto, MultipartFile file) {
        try{
            log.info("Saving profile");
            String url="";
            if(file!=null){
                url = imageClient.save(file).getData().getUrl();
            }

            Profile profile = Profile.builder()
                    .id(getGenerationId())
                    .objective(dto.getObjective())
                    .education(dto.getEducation())
                    .workExperience(dto.getWorkExperience())
                    .typeProfile(TypeProfile.valueOf(dto.getTypeProfile()))
                    .skills(dto.getSkills())
                    .title(dto.getTitle())
                    .contact(dto.getContact())
                    .idUser(dto.getUserId())
                    .url(url)
                    .build();

            return profileRepository.save(profile);
        }catch (DataIntegrityViolationException e){
            throw new CustomException(Error.PROFILE_UNABLE_TO_SAVE);
        }catch (DataAccessException e){
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public List<ProfileDTO> findAllProfiles() {
        return List.of();
    }

    @Override
    public BooleanDTO checkIdProfile(Integer id) {
        log.info("Check id profile: {}",id);
        Optional<Profile> profile = profileRepository.findById(id);
        if(profile.isPresent()){
            return BooleanDTO.builder().isCheck(true).build();
        }
        return BooleanDTO.builder().isCheck(false).build();
    }

    private boolean checkUserId(Integer userId) {
        log.info("Check id user: {}",userId);
        return userClient.checkId(userId);
    }

    @Override
    public ProfileDTO findByIdUser(Integer id){
        log.info("Get profile by idUser: {}", id);
        Query query = new Query();
        query.addCriteria(Criteria.where("idUser").is(id));
        Profile profile = mongoTemplate.findOne(query, Profile.class);
        if(profile != null){
            return covertToDTO(profile);
        }else{
            return null;
        }
    }

    @Override
    public ProfileDTO updateProfile(ProfileDTO profileDTO, MultipartFile file) {
        try{
            log.info("Updating profile is: {}", profileDTO.getId());
            String url="";
            if(file!=null){
                ApiResponse<ImageDTO> response = imageClient.save(file);
                if(response == null || response.getData() == null){
                    log.info("Không lưu ược ảnh hoặc nhận được ImageDto null");
                }else{
                    ImageDTO imageDTO = response.getData();
                    url = imageDTO.getUrl();
                    log.info("Ảnh đã được lưu thành công với url: {}",url);
                }
            }

            log.info("Url: ", url);

            Profile profile = Profile.builder()
                    .id(profileDTO.getId())
                    .objective(profileDTO.getObjective())
                    .education(profileDTO.getEducation())
                    .workExperience(profileDTO.getWorkExperience())
                    .typeProfile(TypeProfile.valueOf(profileDTO.getTypeProfile()))
                    .skills(profileDTO.getSkills())
                    .title(profileDTO.getTitle())
                    .contact(profileDTO.getContact())
                    .idUser(profileDTO.getUserId())
                    .url(url)
                    .build();

            return covertToDTO(profileRepository.save(profile));
        }catch (DataIntegrityViolationException e){
            throw new CustomException(Error.PROFILE_UNABLE_TO_SAVE);
        }catch (DataAccessException e) {
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public ProfileDTO saveProfile(ProfileDTO profileDTO, MultipartFile multipartFile) {
        log.debug("Save profile");
        Profile profile = save(profileDTO,multipartFile);
        return covertToDTO(profile);
    }

    @Override
    public ProfileDTO findById(Integer id) {
        log.info("Get profile by id: {}", id);
        return covertToDTO(profileRepository.findById(id)
                .orElseThrow(() -> new CustomException(Error.PROFILE_NOT_FOUND)));
    }

    @Override
    public List<ProfileDTO> findProfilesByType(TypeProfile typeProfile) {
        try{
            log.info("Get profile by typeProfile: {}", typeProfile.name());
            Query query = new Query();
            query.addCriteria(Criteria.where("typeProfile").in(typeProfile.name()));
            query.limit(20);
            List<Profile> profiles = mongoTemplate.find(query, Profile.class);
            return convertToDTOList(profiles);
        }catch (MongoCommandException e){
            throw new CustomException(Error.MONGO_QUERY_EXECUTION_ERROR);
        }catch (DataAccessException e){
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public ProfileDTO covertToDTO(Profile profile) {
        return modelMapper.map(profile, ProfileDTO.class);
    }

    @Override
    public List<ProfileDTO> findByTitle(String title) {
        try{
            log.info("Get profile by title: {}", title);
            Query query = new Query();
            query.addCriteria(Criteria.where("title").regex(title));
            query.limit(20);
            return convertToDTOList(mongoTemplate.find(query, Profile.class));
        }catch (MongoCommandException e){
            throw new CustomException(Error.MONGO_QUERY_EXECUTION_ERROR);
        }catch(DataAccessException e){
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public List<ProfileDTO> findListProfileIdPendingJob(List<Integer> idJobs) {
        try{
            return convertToDTOList(profileRepository.findByIdIn(idJobs));
        }catch (DataAccessException e){
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }
}
