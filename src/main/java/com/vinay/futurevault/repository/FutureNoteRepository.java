package com.vinay.futurevault.repository;

import com.vinay.futurevault.entity.FutureNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FutureNoteRepository extends JpaRepository<FutureNote, Long> {

    // Scheduler
    List<FutureNote> findByUnlockDateAndUnlockedFalse(LocalDate unlockDate);

    // Get all notes of logged-in user
    List<FutureNote> findByEmail(String email);
}