package com.vinay.futurevault.controller;
import org.springframework.data.domain.Page;
import com.vinay.futurevault.dto.FutureNoteRequest;
import com.vinay.futurevault.service.FutureNoteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.vinay.futurevault.dto.DashboardResponse;
import com.vinay.futurevault.dto.FutureNoteResponse;
import com.vinay.futurevault.dto.NoteQueryRequest;
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
    public FutureNoteResponse saveNote(
            @Valid @RequestBody FutureNoteRequest request) {

        return service.save(request);
    }

    // GET ALL
    @GetMapping
    public Page<FutureNoteResponse> getAllNotes(

            @ModelAttribute NoteQueryRequest request) {

        return service.getNotes(request);
    }
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(service.getDashboard());
    }
    @GetMapping("/search")
    public Page<FutureNoteResponse> searchNotes(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        return service.searchNotes(keyword, page, size);
    }
    // GET BY ID
    @GetMapping("/{id}")
    public FutureNoteResponse getNoteById(@PathVariable Long id) {

        return service.getNoteById(id);
    }
    // UPDATE
    @PutMapping("/{id}")
    public FutureNoteResponse updateNote(
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