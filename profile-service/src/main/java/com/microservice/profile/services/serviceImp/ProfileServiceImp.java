package com.microservice.profile.services.serviceImp;

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
import com.microservice.profile.repository.ProfileRepository;
import com.microservice.profile.services.service.ProfileService;
import com.mongodb.MongoCommandException;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProfileServiceImp implements ProfileService {
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
                .map(project -> modelMapper.map(project, ProfileDTO.class))
                .collect(Collectors.toList());
    }

    public ProfileDTO convertToDTO(Profile profile) {
        return modelMapper.map(profile, ProfileDTO.class);
    }

    public Profile convertToModel(ProfileDTO profileDTO) {
        return modelMapper.map(profileDTO, Profile.class);
    }

    private Profile save(ProfileDTO profileDTO,MultipartFile image) {
        try {
            log.info("Saving profile");
            String url="";
            if(image!=null){
             url=imageClient.save(image).getData().getUrl();
            }
          
            Profile profile = Profile.builder()
                    .id(getGenerationId())
                    .objective(profileDTO.getObjective())
                    .education(profileDTO.getEducation())
                    .workExperience(profileDTO.getWorkExperience())
                    .typeProfile(TypeProfile.valueOf(profileDTO.getTypeProfile()))
                    .skills(profileDTO.getSkills())
                    .title(profileDTO.getTitle())
                    .contact(profileDTO.getContact())
                    .idUser(profileDTO.getIdUser())
                    .url(url)
                    .build();
            return profileRepository.insert(profile);
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(Error.PROFILE_UNABLE_TO_SAVE);
        } catch (DataAccessException e) {
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    public Integer getGenerationId() {
        UUID uuid = UUID.randomUUID();
        return (int) (uuid.getMostSignificantBits() & 0xFFFFFFFFL);
    }

    @Override
    public ProfileDTO saveProfile(ProfileDTO profileDTO,MultipartFile imageFile) {
        log.debug("Save profile");
        Profile profile = save(profileDTO,imageFile);
        return convertToDTO(profile);
    }

    @Override
    public ProfileDTO updateProfile(ProfileDTO profileDTO,MultipartFile imageFile) {
        try {

            log.info("Updating profile id: {}", profileDTO.getId());
            String url = "";
            if (imageFile != null) {
                ApiResponse<ImageDTO> response = imageClient.save(imageFile);
                if (response == null || response.getData() == null) {
                    log.info("Không lưu được ảnh hoặc nhận được ImageDTO null");
                } else {
                    ImageDTO imageDTO = response.getData();
                    url = imageDTO.getUrl();
                    log.info("Ảnh đã được lưu thành công với URL: {}", url);
                }
            }
            
            log.info("url", url);

            Profile profile= Profile.builder()
                    .id(profileDTO.getId())
                    .objective(profileDTO.getObjective())
                    .education(profileDTO.getEducation())
                    .workExperience(profileDTO.getWorkExperience())
                    .typeProfile(TypeProfile.valueOf(profileDTO.getTypeProfile()))
                    .skills(profileDTO.getSkills())
                    .title(profileDTO.getTitle())
                    .contact(profileDTO.getContact())
                    .idUser(profileDTO.getIdUser())
                    .url(url)
                    .build();
                    return convertToDTO(profileRepository.save(profile));
            
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(Error.PROFILE_UNABLE_TO_UPDATE);
        } catch (DataAccessException e) {
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public ProfileDTO findById(Integer id) {
        log.info("Get profile by id: {}", id);
        return convertToDTO(profileRepository.findById(id)
                .orElseThrow(() -> new CustomException(Error.PROFILE_NOT_FOUND)));
    }

    @Override
    public List<ProfileDTO> findProfilesByType(TypeProfile typeProfile) {
        try {
            log.info("Get profile by type: {}", typeProfile.name());
            Query query = new Query();
            query.addCriteria(Criteria.where("typeProfile").in(typeProfile.name()));
            query.limit(20);
            List<Profile> profiles = mongoTemplate.find(query, Profile.class);
            return convertToDTOList(profiles);
        } catch (MongoCommandException e) {
            throw new CustomException(Error.MONGO_QUERY_EXECUTION_ERROR);
        } catch (DataAccessException e) {
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public List<ProfileDTO> getAllProfile() {
        try {
            log.info("Get all profiles");
            Query query = new Query();
            query.limit(20);
            return convertToDTOList(mongoTemplate.find(query, Profile.class));
        } catch (DataAccessException e) {
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public List<ProfileDTO> findByTitle(String title) {
        try {
            log.info("Get profile by title: {}", title);
            Query query = new Query();
            query.addCriteria(Criteria.where("title").regex(title));
            query.limit(20);
            return convertToDTOList(mongoTemplate.find(query, Profile.class));
        } catch (MongoCommandException e) {
            throw new CustomException(Error.MONGO_QUERY_EXECUTION_ERROR);
        } catch (DataAccessException e) {
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public BooleanDTO checkIdProfile(Integer id) {
        log.info("Check id profile: {}", id);
        Optional<Profile> profile = profileRepository.findById(id);
        if(profile.isPresent()){
            return BooleanDTO.builder().isCheck(true).build();
        }
        return BooleanDTO.builder().isCheck(false).build();
    }

    private boolean checkUserId(Integer id) {
        log.info("Check id user: {}", id);
        return userClient.checkId(id);
    }

    @Override
    public ProfileDTO findByIdUser(Integer id) {
            log.info("Get profile by idUser: {}", id);
            Query query = new Query();
            query.addCriteria(Criteria.where("idUser").is(id));
            Profile profile = mongoTemplate.findOne(query, Profile.class);
            if (profile!= null) {
                return convertToDTO(profile);
            } else {
                return null;
            }
    }

    @Override
    public List<ProfileDTO> findListProfileByIdPendingJob(List<Integer> idJobs) {
        try {
            return convertToDTOList(profileRepository.findByIdIn(idJobs));
        } catch (DataAccessException e) {
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }


}
