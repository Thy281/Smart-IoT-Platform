package com.smartgym.member.adapter.in.web;

import com.smartgym.member.domain.model.EnrolmentStatus;
import com.smartgym.member.domain.model.Member;
import com.smartgym.member.domain.model.MemberStatus;

import java.time.Instant;
import java.util.UUID;

public record MemberResponse(
        UUID id,
        String fullName,
        String email,
        String phone,
        MemberStatus status,
        EnrolmentStatus enrolmentStatus,
        Instant createdAt
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getFullName(),
                member.getEmail(),
                member.getPhone(),
                member.getStatus(),
                member.getEnrolmentStatus(),
                member.getCreatedAt()
        );
    }
}
