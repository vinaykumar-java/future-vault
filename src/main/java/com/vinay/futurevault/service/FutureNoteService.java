package com.vinay.futurevault.service;

import com.vinay.futurevault.dto.DashboardResponse;
import com.vinay.futurevault.dto.FutureNoteRequest;
import com.vinay.futurevault.dto.FutureNoteResponse;
import com.vinay.futurevault.dto.NoteQueryRequest;
import com.vinay.futurevault.entity.FutureNote;
import com.vinay.futurevault.exception.ResourceNotFoundException;
import com.vinay.futurevault.repository.FutureNoteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
    // CREATE
    public FutureNoteResponse save(FutureNoteRequest request) {

        FutureNote note = new FutureNote();

        note.setTitle(request.getTitle());
        note.setMessage(request.getMessage());
        note.setUnlockDate(request.getUnlockDate());

        note.setEmail(getLoggedInUserEmail());

        note.setCreatedAt(LocalDateTime.now());
        note.setUnlocked(false);

        FutureNote savedNote = repository.save(note);

        return mapToResponse(savedNote);
    }

    // GET ALL
    public List<FutureNoteResponse> getAllNotes() {

        return repository.findByEmail(getLoggedInUserEmail())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // GET BY ID
    // GET BY ID
    public FutureNoteResponse getNoteById(Long id) {

        FutureNote note = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Future Note with id " + id + " not found"));

        if (!note.getEmail().equals(getLoggedInUserEmail())) {
            throw new AccessDeniedException(
                    "You are not allowed to access this note.");
        }

        return mapToResponse(note);
    }

    // UPDATE
    // UPDATE
    public FutureNoteResponse updateNote(Long id, FutureNoteRequest request) {

        FutureNote existing = getNoteEntity(id);

        existing.setTitle(request.getTitle());
        existing.setMessage(request.getMessage());
        existing.setUnlockDate(request.getUnlockDate());

        FutureNote updated = repository.save(existing);

        return mapToResponse(updated);
    }

    // DELETE
    // DELETE
    public void deleteNote(Long id) {

        FutureNote existing = getNoteEntity(id);

        repository.delete(existing);
    }
    // DASHBOARD
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

    // SEARCH
    public Page<FutureNoteResponse> searchNotes(String keyword, int page, int size) {

        String email = getLoggedInUserEmail();

        Pageable pageable = PageRequest.of(page, size);

        Page<FutureNote> notes = repository
                .findByEmailAndTitleContainingIgnoreCaseOrEmailAndMessageContainingIgnoreCase(
                        email,
                        keyword,
                        email,
                        keyword,
                        pageable
                );

        return notes.map(this::mapToResponse);
    }

    // PAGINATION + SORTING + FILTER
    // PAGINATION + SORTING + FILTER
    public Page<FutureNoteResponse> getNotes(NoteQueryRequest request) {

        String email = getLoggedInUserEmail();

        Sort sort = request.getDirection().equalsIgnoreCase("desc")
                ? Sort.by(request.getSortBy()).descending()
                : Sort.by(request.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                sort
        );

        Page<FutureNote> notes;

        String status = request.getStatus();

        if (status.equalsIgnoreCase("locked")) {

            notes = repository.findByEmailAndUnlocked(
                    email,
                    false,
                    pageable
            );

        } else if (status.equalsIgnoreCase("unlocked")) {

            notes = repository.findByEmailAndUnlocked(
                    email,
                    true,
                    pageable
            );

        } else {

            notes = repository.findByEmail(
                    email,
                    pageable
            );
        }

        return notes.map(this::mapToResponse);
    }

    // ENTITY -> RESPONSE DTO
    private FutureNoteResponse mapToResponse(FutureNote note) {

        FutureNoteResponse response = new FutureNoteResponse();

        response.setId(note.getId());
        response.setTitle(note.getTitle());
        response.setMessage(note.getMessage());
        response.setUnlockDate(note.getUnlockDate());
        response.setCreatedAt(note.getCreatedAt());

        response.setStatus(
                note.isUnlocked()
                        ? "UNLOCKED"
                        : "LOCKED"
        );

        return response;
    }
    private FutureNote getNoteEntity(Long id) {

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
}