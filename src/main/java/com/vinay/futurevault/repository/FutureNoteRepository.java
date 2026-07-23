package com.vinay.futurevault.repository;

import com.vinay.futurevault.entity.FutureNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FutureNoteRepository extends JpaRepository<FutureNote, Long> {

    // Existing Methods
    List<FutureNote> findByEmail(String email);

    List<FutureNote> findByUnlockDateAndUnlockedFalse(LocalDate unlockDate);

    // Dashboard
    long countByEmail(String email);

    long countByEmailAndUnlockedFalse(String email);

    long countByEmailAndUnlockedTrue(String email);

    // Search
    List<FutureNote> findByEmailAndTitleContainingIgnoreCaseOrEmailAndMessageContainingIgnoreCase(
            String email,
            String title,
            String email2,
            String message
    );
}