package com.smartgym.member.adapter.out.persistence;

import com.smartgym.member.application.port.out.MemberRepository;
import com.smartgym.member.domain.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MemberPersistenceAdapter implements MemberRepository {

    private final SpringDataMemberRepository repository;

    @Override
    public Member save(Member member) {
        var entity = toEntity(member);
        var saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Member> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return repository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public List<Member> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    private MemberJpaEntity toEntity(Member m) {
        return MemberJpaEntity.builder()
                .id(m.getId())
                .fullName(m.getFullName())
                .email(m.getEmail())
                .phone(m.getPhone())
                .status(m.getStatus())
                .enrolmentStatus(m.getEnrolmentStatus())
                .photoPath(m.getPhotoPath())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .build();
    }

    private Member toDomain(MemberJpaEntity e) {
        return Member.reconstitute(
                e.getId(),
                e.getFullName(),
                e.getEmail(),
                e.getPhone(),
                e.getStatus(),
                e.getEnrolmentStatus(),
                e.getPhotoPath(),
                e.getCreatedAt(),
                e.getUpdatedAt());
    }
}
