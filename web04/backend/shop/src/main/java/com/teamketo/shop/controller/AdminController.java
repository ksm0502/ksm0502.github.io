package com.teamketo.shop.controller;

import com.teamketo.shop.entity.Member;
import com.teamketo.shop.entity.Role;
import com.teamketo.shop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//최고 관리자
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final MemberService memberService;
    //전체 회원 조회
    @GetMapping("/members")
    public ResponseEntity<List<Member>> getAllMember() {
        return ResponseEntity.ok(memberService.findAll());
    }

    //일반 회원 조회
    @GetMapping("/users")
    public ResponseEntity<List<Member>> getUsers() {
        return ResponseEntity.ok(memberService.findByRole(Role.USER));
    }

    //매니저 조회
    @GetMapping("/managers")
    public ResponseEntity<List<Member>> getManagers() {
        return ResponseEntity.ok(memberService.findByRole(Role.MANAGER));
    }

    //권한 변경
    @PutMapping("/members/{id}/role")
    public ResponseEntity<Void> changeRole(@PathVariable Long id, @RequestParam Role role) {
        memberService.changeRole(id, role);
        return ResponseEntity.ok().build();
    }
    
    //회원 강퇴
    @DeleteMapping("/members/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.ok().build();
    }

    //특정 회원 정보 조회
    @GetMapping("/member/{email}")
    public ResponseEntity<Member> getMemberDetail(@PathVariable String email) {
        return ResponseEntity.ok(memberService.findByEmail(email));
    }
}
