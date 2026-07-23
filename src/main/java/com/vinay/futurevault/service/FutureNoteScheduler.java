package com.vinay.futurevault.service;

import com.vinay.futurevault.entity.FutureNote;
import com.vinay.futurevault.repository.FutureNoteRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FutureNoteScheduler {

    private final FutureNoteRepository repository;
    private final EmailService emailService;

    public FutureNoteScheduler(FutureNoteRepository repository,
                               EmailService emailService) {
        this.repository = repository;
        this.emailService = emailService;
    }

    @Scheduled(fixedRate = 60000) // Runs every 60 seconds
    public void unlockFutureNotes() {

        List<FutureNote> notes =
                repository.findByUnlockDateAndUnlockedFalse(LocalDate.now());

        for (FutureNote note : notes) {

            emailService.sendFutureNote(
                    note.getEmail(),
                    note.getTitle(),
                    note.getMessage()
            );

            note.setUnlocked(true);

            repository.save(note);

            System.out.println("Email sent to: " + note.getEmail());
        }
    }
}