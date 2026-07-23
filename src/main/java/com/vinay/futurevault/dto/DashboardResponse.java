package com.vinay.futurevault.dto;

public class DashboardResponse {

    private long totalNotes;
    private long lockedNotes;
    private long unlockedNotes;

    public DashboardResponse() {
    }

    public DashboardResponse(long totalNotes, long lockedNotes, long unlockedNotes) {
        this.totalNotes = totalNotes;
        this.lockedNotes = lockedNotes;
        this.unlockedNotes = unlockedNotes;
    }

    public long getTotalNotes() {
        return totalNotes;
    }

    public void setTotalNotes(long totalNotes) {
        this.totalNotes = totalNotes;
    }

    public long getLockedNotes() {
        return lockedNotes;
    }

    public void setLockedNotes(long lockedNotes) {
        this.lockedNotes = lockedNotes;
    }

    public long getUnlockedNotes() {
        return unlockedNotes;
    }

    public void setUnlockedNotes(long unlockedNotes) {
        this.unlockedNotes = unlockedNotes;
    }
}