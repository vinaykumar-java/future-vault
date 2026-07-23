package com.vinay.futurevault.service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.vinay.futurevault.dto.FutureNoteRequest;
import com.vinay.futurevault.entity.FutureNote;
import com.vinay.futurevault.exception.ResourceNotFoundException;
import com.vinay.futurevault.repository.FutureNoteRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.vinay.futurevault.dto.DashboardResponse;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FutureNoteService {

    private final FutureNoteRepository repository;

    public FutureNoteService(FutureNoteRepository repository) {
        this.repository = repository;
    }

    // Logged-in user's email
    private String getLoggedInUserEmail() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return authentication.getName();
    }

    // CREATE
    public FutureNote save(FutureNoteRequest request) {

        FutureNote note = new FutureNote();

        note.setTitle(request.getTitle());
        note.setMessage(request.getMessage());
        note.setUnlockDate(request.getUnlockDate());

        note.setEmail(getLoggedInUserEmail());

        note.setCreatedAt(LocalDateTime.now());
        note.setUnlocked(false);

        return repository.save(note);
    }

    // GET ALL
    public List<FutureNote> getAllNotes() {

        return repository.findByEmail(getLoggedInUserEmail());
    }

    // GET BY ID
    public FutureNote getNoteById(Long id) {

        FutureNote note = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Future Note with id " + id + " not found"));

        if (!note.getEmail().equals(getLoggedInUserEmail())) {
            throw new AccessDeniedException(
                    "You are not allowed to access this note.");
        }

        return note;
    }

    // UPDATE
    public FutureNote updateNote(Long id, FutureNoteRequest request) {

        FutureNote existing = getNoteById(id);

        existing.setTitle(request.getTitle());
        existing.setMessage(request.getMessage());
        existing.setUnlockDate(request.getUnlockDate());

        return repository.save(existing);
    }

    // DELETE
    public void deleteNote(Long id) {

        FutureNote existing = getNoteById(id);

        repository.delete(existing);
    }
    public DashboardResponse getDashboard() {

        String email = getLoggedInUserEmail();

        long totalNotes = repository.countByEmail(email);

        long lockedNotes = repository.countByEmailAndUnlockedFalse(email);

        long unlockedNotes = repository.countByEmailAndUnlockedTrue(email);

        return new DashboardResponse(
                totalNotes,
                lockedNotes,
                unlockedNotes
        );
    }
    public List<FutureNote> searchNotes(String keyword) {

        String email = getLoggedInUserEmail();

        return repository.findByEmailAndTitleContainingIgnoreCaseOrEmailAndMessageContainingIgnoreCase(
                email,
                keyword,
                email,
                keyword
        );
    }
    public Page<FutureNote> getNotes(int page, int size) {

        String email = getLoggedInUserEmail();

        Pageable pageable = PageRequest.of(page, size);

        return repository.findByEmail(email, pageable);
    }
}