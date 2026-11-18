package com.teamketo.shop.controller;

import com.teamketo.shop.dto.MemberResponseDto;
import com.teamketo.shop.entity.Member;
import com.teamketo.shop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    // 현재 로그인된 사용자 정보 (세션 복원용)
    @GetMapping("/member/me")
    public ResponseEntity<MemberResponseDto> getMe(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(new MemberResponseDto(memberService.requireCurrentMember(principal.getName())));
    }

    // 회원 가입
    @PostMapping("/signup")
    public ResponseEntity<MemberResponseDto> signup(@RequestBody Member member) {
        return ResponseEntity.ok(new MemberResponseDto(memberService.signup(member)));
    }

    // 내 정보 조회
    @GetMapping("/{email}")
    public ResponseEntity<MemberResponseDto> getMember(@PathVariable String email, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        if (!principal.getName().equals(email)) {
            throw new AccessDeniedException("본인 회원 정보만 조회할 수 있습니다.");
        }
        return ResponseEntity.ok(new MemberResponseDto(memberService.requireCurrentMember(principal.getName())));
    }

    // 회원 정보 수정
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateMember(@PathVariable Long id, @RequestBody Member memberData,
                                             Principal principal) {
        Member currentMember = memberService.requireOwnMember(principal.getName(), id);
        memberService.updateMember(currentMember.getEmail(), memberData.getName(), memberData.getPhone(), memberData.getAddress());
        return ResponseEntity.ok().build();
    }

    // 비밀번호 변경
    @PutMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @RequestParam String password,
                                               Principal principal) {
        Member currentMember = memberService.requireOwnMember(principal.getName(), id);
        memberService.changePassword(currentMember.getEmail(), password);
        return ResponseEntity.ok().build();
    }

    // 회원 탈퇴
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id, Principal principal) {
        Member currentMember = memberService.requireOwnMember(principal.getName(), id);
        memberService.deleteMember(currentMember.getId());
        return ResponseEntity.ok().build();
    }
}
