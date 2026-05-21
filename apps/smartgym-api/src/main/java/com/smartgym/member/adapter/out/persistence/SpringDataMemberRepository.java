package com.smartgym.member.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface SpringDataMemberRepository extends JpaRepository<MemberJpaEntity, UUID> {

    Optional<MemberJpaEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
