package com.smartgym.member.application.port.in;

import com.smartgym.member.domain.model.Member;

import java.util.UUID;

/**
 * Input port for registering a new gym member.
 */
public interface RegisterMemberUseCase {

    Member register(RegisterMemberCommand command);

    record RegisterMemberCommand(
            String fullName,
            String email,
            String phone
    ) {}
}
