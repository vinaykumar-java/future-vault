package com.vinay.futurevault.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class FutureNoteResponse {

    private Long id;

    private String title;

    private String message;

    private LocalDate unlockDate;

    private LocalDateTime createdAt;

    private String status;

    public FutureNoteResponse() {
    }

    public FutureNoteResponse(Long id,
                              String title,
                              String message,
                              LocalDate unlockDate,
                              LocalDateTime createdAt,
                              String status) {

        this.id = id;
        this.title = title;
        this.message = message;
        this.unlockDate = unlockDate;
        this.createdAt = createdAt;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDate getUnlockDate() {
        return unlockDate;
    }

    public void setUnlockDate(LocalDate unlockDate) {
        this.unlockDate = unlockDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}