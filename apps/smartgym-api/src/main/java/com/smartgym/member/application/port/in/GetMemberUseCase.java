package com.smartgym.member.application.port.in;

import com.smartgym.member.domain.model.Member;

import java.util.List;
import java.util.UUID;

/**
 * Input port for querying member data.
 */
public interface GetMemberUseCase {

    Member getById(UUID id);

    List<Member> getAll();
}
