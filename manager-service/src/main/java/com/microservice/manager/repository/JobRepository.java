package com.microservice.manager.repository;

import com.microservice.manager.models.Job;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends MongoRepository<Job, Integer> {

}
