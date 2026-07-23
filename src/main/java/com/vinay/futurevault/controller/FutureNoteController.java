package com.vinay.futurevault.controller;

import com.vinay.futurevault.dto.FutureNoteRequest;
import com.vinay.futurevault.entity.FutureNote;
import com.vinay.futurevault.service.FutureNoteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.vinay.futurevault.dto.DashboardResponse;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class FutureNoteController {

    private final FutureNoteService service;

    public FutureNoteController(FutureNoteService service) {
        this.service = service;
    }

    // CREATE
    @PostMapping
    public FutureNote saveNote(@Valid @RequestBody FutureNoteRequest request) {
        return service.save(request);
    }

    // GET ALL
    @GetMapping
    public List<FutureNote> getAllNotes() {
        return service.getAllNotes();
    }
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(service.getDashboard());
    }
    @GetMapping("/search")
    public List<FutureNote> searchNotes(@RequestParam String keyword) {

        return service.searchNotes(keyword);
    }
    // GET BY ID
    @GetMapping("/{id}")
    public FutureNote getNoteById(@PathVariable Long id) {
        return service.getNoteById(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public FutureNote updateNote(
            @PathVariable Long id,
            @Valid @RequestBody FutureNoteRequest request) {

        return service.updateNote(id, request);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String deleteNote(@PathVariable Long id) {

        service.deleteNote(id);

        return "Future Note deleted successfully!";
    }
}