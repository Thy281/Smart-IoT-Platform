package com.smartgym.member.application.port.out;

import com.smartgym.member.domain.model.Member;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Output port for member persistence operations.
 * Implemented by a driven adapter (JPA, in-memory, etc.).
 */
public interface MemberRepository {

    Member save(Member member);

    Optional<Member> findById(UUID id);

    Optional<Member> findByEmail(String email);

    List<Member> findAll();

    boolean existsByEmail(String email);
}
