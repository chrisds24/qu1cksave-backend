package com.qu1cksave.qu1cksave_backend.resume;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ResumeRepository extends JpaRepository<Resume, UUID> {
    Optional<Resume> findByIdAndMemberId(UUID id, UUID memberId);

    Integer deleteByIdAndMemberId(UUID id, UUID memberId);
}
