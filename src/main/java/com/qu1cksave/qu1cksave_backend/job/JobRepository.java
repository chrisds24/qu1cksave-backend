package com.qu1cksave.qu1cksave_backend.job;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {
    // TODO: Search "log spring data jpa repository result"

    // https://stackoverflow.com/questions/64762080/how-to-map-sql-native-query-result-into-dto-in-spring-jpa-repository
    // - Regarding NamedNativeQuery and SqlResultSetMapping
    @NativeQuery(value = """ 
        SELECT
            j.*,
            json_build_object(
              'id', r.id,
              'member_id', r.member_id,
              'file_name', r.file_name,
              'mime_type', r.mime_type
            ) AS resume,
            json_build_object(
              'id', c.id,
              'member_id', c.member_id,
              'file_name', c.file_name,
              'mime_type', c.mime_type
            ) AS cover_letter
        FROM
            job j
            LEFT JOIN resume r ON j.resume_id = r.id AND j.member_id = r.member_id
            LEFT JOIN cover_letter c ON j.cover_letter_id = c.id AND j.member_id = c.member_id
        WHERE j.member_id = ?1
    """)
    List<ResponseJobDto> findByMemberIdWithFiles(UUID memberId);
    // https://docs.spring.io/spring-data/jpa/reference/repositories/projections.html#projections.dtos
    // - About projections
    // https://stackoverflow.com/questions/37111445/spring-data-jpa-classcastexception-integer-cannot-be-cast-to-long
    // List<JobDto> findByMemberIdWithFiles(@Param("memberId") UUID memberId);
    // - The code above also works.
    // - Replace "WHERE j.member_id = ?1" with "WHERE j.member_id =:memberId"

    // https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html#jpa.modifying-queries.derived-delete
    // - Derived delete query to specify params to delete by + the return val
    // - DOESN'T WORK. It returns an Integer
//    Optional<Job> deleteByIdAndMemberId(UUID id, UUID memberId);
    // TODO: (5/12/25) Fix the error below
    //  Modifying queries can only use void or int/Integer as return type; Offending method: public abstract java.util.Optional com.qu1cksave.qu1cksave_backend.job.JobRepository.deleteByIdAndMemberIdReturningJob(java.util.UUID,java.util.UUID)
    @Modifying
    @NativeQuery(value = "DELETE FROM job WHERE id = ?1 AND member_id = ?2 RETURNING *")
    Optional<ResponseJobDto> deleteByIdAndMemberIdReturningJob(UUID id, UUID memberId);

    // Keep for reference
//    List<Job> findByMemberId(UUID memberId);
//    Optional<Job> findByIdAndMemberId(UUID id, UUID memberId);
}

// https://stackoverflow.com/questions/51626520/whether-using-queries-in-jpa-repositories-is-proper-practice
// - Has a @Query to do a JOIN
// https://stackoverflow.com/questions/42966967/creating-a-custom-query-with-spring-data-jpa
// - Has example of native query using @Query(nativeQuery=true)
// https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html
// - @Query(nativeQuery=true), @NativeQuery, Named parameters
// https://stackoverflow.com/questions/42966967/creating-a-custom-query-with-spring-data-jpa
// - If you really need such behaviour, then you need to get EntityManager
//   and run queries using it directly. As a reference, see this
//   answer: stackoverflow.com/a/15341601/187241.
// https://www.reddit.com/r/SpringBoot/comments/1ez5zeg/jpa_should_we_use_query_always/
// - Its okay to use both the JPA provided methods and @Query/@NativeQuery
//   depending on what's needed
// https://stackoverflow.com/questions/66711228/how-to-create-a-native-query-in-jpa
// - Example Postres native query.
// https://thorben-janssen.com/native-queries-with-spring-data-jpa/
// - Because write operations need to be executed differently than read\
//   operations, you also need to annotate the repository method with a @Modifying annotation.
// - TODO: (3/28/2025) This might be important for create/update/delete
