package com.qu1cksave.qu1cksave_backend.job;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {
    List<Job> findByMemberId(UUID memberId);
    Optional<Job> findByIdAndMemberId(UUID id, UUID memberId);

    // https://stackoverflow.com/questions/51626520/whether-using-queries-in-jpa-repositories-is-proper-practice
    // - Has a @Query to do a JOIN
    // https://stackoverflow.com/questions/42966967/creating-a-custom-query-with-spring-data-jpa
    // - Has example of native query using @Query(nativeQuery=true)
    // https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html
    // - Has @Query info
    // - @Query(nativeQuery=true)
    // - @NativeQuery
    // - Named parameters
    // - Activating Hibernate comments
    //   -- spring.jpa.properties.hibernate.use_sql_comments=true
}
