package com.vinay.futurevault.repository;

import com.vinay.futurevault.entity.FutureNote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FutureNoteRepository extends JpaRepository<FutureNote, Long> {

    List<FutureNote> findByEmail(String email);

    List<FutureNote> findByUnlockDateAndUnlockedFalse(LocalDate unlockDate);

    long countByEmail(String email);

    long countByEmailAndUnlockedFalse(String email);

    long countByEmailAndUnlockedTrue(String email);

    Page<FutureNote> findByEmailAndTitleContainingIgnoreCaseOrEmailAndMessageContainingIgnoreCase(
            String email,
            String title,
            String email2,
            String message,
            Pageable pageable
    );

    Page<FutureNote> findByEmail(
            String email,
            Pageable pageable
    );

    Page<FutureNote> findByEmailAndUnlocked(
            String email,
            boolean unlocked,
            Pageable pageable
    );
}