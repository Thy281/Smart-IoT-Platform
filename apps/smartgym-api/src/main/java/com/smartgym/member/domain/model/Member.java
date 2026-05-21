package com.smartgym.member.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Member aggregate root.
 * Pure domain object — no JPA or framework annotations here.
 */
public class Member {

    private final UUID id;
    private String fullName;
    private String email;
    private String phone;
    private MemberStatus status;
    private EnrolmentStatus enrolmentStatus;
    private String photoPath;
    private Instant createdAt;
    private Instant updatedAt;

    public Member(UUID id, String fullName, String email, String phone) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.status = MemberStatus.ACTIVE;
        this.enrolmentStatus = EnrolmentStatus.NOT_ENROLLED;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    /**
     * Reconstruction constructor for use by persistence adapters only.
     * Preserves all persisted state without triggering domain invariants.
     */
    public static Member reconstitute(
            UUID id,
            String fullName,
            String email,
            String phone,
            MemberStatus status,
            EnrolmentStatus enrolmentStatus,
            String photoPath,
            Instant createdAt,
            Instant updatedAt) {
        var member = new Member(id, fullName, email, phone);
        member.status = status;
        member.enrolmentStatus = enrolmentStatus;
        member.photoPath = photoPath;
        member.createdAt = createdAt;
        member.updatedAt = updatedAt;
        return member;
    }

    public void updateProfile(String fullName, String email, String phone) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.updatedAt = Instant.now();
    }

    public void markEnrolled() {
        this.enrolmentStatus = EnrolmentStatus.ENROLLED;
        this.updatedAt = Instant.now();
    }

    public void markEnrolmentFailed() {
        this.enrolmentStatus = EnrolmentStatus.FAILED;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.status = MemberStatus.INACTIVE;
        this.updatedAt = Instant.now();
    }

    // ─── Getters ────────────────────────────────────────────

    public UUID getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public MemberStatus getStatus() { return status; }
    public EnrolmentStatus getEnrolmentStatus() { return enrolmentStatus; }
    public String getPhotoPath() { return photoPath; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
        this.updatedAt = Instant.now();
    }
}
