package com.microservice.profile;

import com.microservice.profile.model.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProfileRepository extends MongoRepository<Profile, Integer> {
    List<Profile> findByIdIn(List<Integer> ids) ;
}
