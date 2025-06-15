package com.microservice.profile.services.service;

import com.microservice.profile.dto.BooleanDTO;
import com.microservice.profile.dto.ProfileDTO;
import com.microservice.profile.model.Profile;
import com.microservice.profile.model.TypeProfile;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ProfileService {
    ProfileDTO updateProfile(ProfileDTO profileDTO,MultipartFile imageFile);
    ProfileDTO saveProfile(ProfileDTO profileDTO,MultipartFile imageFile);
    ProfileDTO findById(Integer id);
    ProfileDTO findByIdUser(Integer id);
    List<ProfileDTO> findProfilesByType(TypeProfile typeProfile);
    ProfileDTO convertToDTO(Profile profile);
    Profile convertToModel(ProfileDTO profileDTO);
    List<ProfileDTO> getAllProfile();
    List<ProfileDTO> findByTitle(String title);
    List<ProfileDTO> findListProfileByIdPendingJob(List<Integer> idJobs);
    BooleanDTO checkIdProfile(Integer id);
    
}