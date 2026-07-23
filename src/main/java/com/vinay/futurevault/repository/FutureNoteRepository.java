package com.vinay.futurevault.repository;

import com.vinay.futurevault.entity.FutureNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;

public interface FutureNoteRepository extends JpaRepository<FutureNote, Long> {

    // Existing Methods
    List<FutureNote> findByEmail(String email);
    Page<FutureNote> findByEmail(String email, Pageable pageable);
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