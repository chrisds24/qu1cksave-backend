package com.qu1cksave.qu1cksave_backend.job;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {
    // 'findById(UUID)' in 'com. qu1cksave. qu1cksave_backend. job. JobRepository' clashes with 'findById(ID)' in 'org. springframework. data. repository. CrudRepository'; attempting to use incompatible return type
//    Job findById(UUID id);
}
