package com.microservice.profile.service.inter;

import com.microservice.profile.dto.BooleanDTO;
import com.microservice.profile.dto.ProfileDTO;
import com.microservice.profile.model.Profile;
import com.microservice.profile.model.TypeProfile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProfileService {
    ProfileDTO updateProfile(ProfileDTO profileDTO, MultipartFile file);
    ProfileDTO saveProfile(ProfileDTO profileDTO, MultipartFile multipartFile);
    ProfileDTO findById(Integer id);
    ProfileDTO findByIdUser(Integer id);
    List<ProfileDTO> findProfilesByType(TypeProfile typeProfile);
    ProfileDTO covertToDTO(Profile profile);
    Profile convertToModel(ProfileDTO profileDTO);
    List<ProfileDTO> findAllProfiles();
    List<ProfileDTO> findByTitle(String title);
    List<ProfileDTO> findListProfileIdPendingJob(List<Integer> idJobs);
    BooleanDTO checkIdProfile(Integer id);
}
