package com.smartgym.member.application.service;

import com.smartgym.member.application.port.in.GetMemberUseCase;
import com.smartgym.member.application.port.in.RegisterMemberUseCase;
import com.smartgym.member.application.port.out.MemberRepository;
import com.smartgym.member.domain.model.Member;
import com.smartgym.shared.exception.BusinessException;
import com.smartgym.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService implements RegisterMemberUseCase, GetMemberUseCase {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Member register(RegisterMemberCommand command) {
        if (memberRepository.existsByEmail(command.email())) {
            throw new BusinessException("A member with email '" + command.email() + "' already exists");
        }
        var member = new Member(UUID.randomUUID(), command.fullName(), command.email(), command.phone());
        var saved = memberRepository.save(member);
        log.info("Registered new member id={} email={}", saved.getId(), saved.getEmail());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Member getById(UUID id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Member> getAll() {
        return memberRepository.findAll();
    }
}
