package com.qu1cksave.qu1cksave_backend.coverletter;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CoverLetterRepository extends JpaRepository<CoverLetter, UUID> {
    Optional<CoverLetter> findByIdAndMemberId(UUID id, UUID memberId);

    Integer deleteByIdAndMemberId(UUID id, UUID memberId);
}
