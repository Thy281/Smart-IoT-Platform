package com.smartgym.member.adapter.in.web;

import com.smartgym.member.application.port.in.GetMemberUseCase;
import com.smartgym.member.application.port.in.RegisterMemberUseCase;
import com.smartgym.member.domain.model.Member;
import com.smartgym.shared.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final RegisterMemberUseCase registerMemberUseCase;
    private final GetMemberUseCase getMemberUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponse>> register(
            @Valid @RequestBody RegisterMemberRequest request) {
        var member = registerMemberUseCase.register(
                new RegisterMemberUseCase.RegisterMemberCommand(
                        request.fullName(),
                        request.email(),
                        request.phone()));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Member registered successfully", MemberResponse.from(member)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberResponse>> getById(@PathVariable UUID id) {
        var member = getMemberUseCase.getById(id);
        return ResponseEntity.ok(ApiResponse.ok(MemberResponse.from(member)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MemberResponse>>> getAll() {
        List<MemberResponse> members = getMemberUseCase.getAll()
                .stream()
                .map(MemberResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(members));
    }
}
