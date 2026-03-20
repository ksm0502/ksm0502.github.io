package com.teamketo.shop.service;

import com.teamketo.shop.entity.Member;
import com.teamketo.shop.entity.Role;
import com.teamketo.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    //회원가입
    public Member signup(Member member) {
        if(memberRepository.existsByEmail(member.getEmail())){
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }
        //비밀번호 암호화
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        member.setRole(Role.USER);

        return memberRepository.save(member);
    }

    public Member findByUsername(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() ->
                new RuntimeException("회원이 존재하지 않습니다."));
    }

    //이메일로 회원 조회
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() ->
                new RuntimeException("회원이 존재하지 않습니다."));
    }

    //회원 목록 조회(관리자)
    public List<Member> findAll() {
        return memberRepository.findAll();
    }
    
    //특정 롤(Role) 조회 =>role(ADMIN, MANAGER, USER) = ROLE_GUEST
    public List<Member> findByRole(Role role) {
        List<Member> members = memberRepository.findByRole(role);
        if (members.isEmpty()) {
            throw new RuntimeException("해당 권한을 가진 회원이 없습니다.");
        }
        return members;
    }
    
    //회원 정보 수정 -> findById() 또는 findByEmail + save
    //회원이 수정을 위한 입력된 정보를 받아 기존의 회원 정보를 변경(Setter)한 후 save()
    public void updateMember(String email, String name, String phone, String address) {
        Member member = findByEmail(email);
        member.setName(name);
        member.setPhone(phone);
        member.setAddress(address);
        memberRepository.save(member);
    }

    
    //비밀번호 변경 -> findById() 또는 findByEmail + save
    //회원이 수정을 위한 본인의 비밀번호를 받아 기존의 회원 정보 중에서 비밀번호(setPassword)를 암호화하여 save()
    public void changePassword(String email, String newPassword) {
        Member member = findByEmail(email);
        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
    }


    //롤 변경(Admin 전용)
    //관리자가 회원의 수정한 등급(Role)을 입력받아 기존의 회원 정보 중에서 비밀번호(setPassword)를 암호화하여 save()
    public void changeRole(Long id, Role role) {
        Member member = memberRepository.findById(id).orElseThrow(() ->
                new RuntimeException("회원을 찾을 수 없습니다."));
        member.setRole(role);
        memberRepository.save(member);
    }

    //회원 탈퇴/강퇴
    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

    //이메일별 회원 목록 조회
    public List<Member> findByEmailMember(String email) {
        return memberRepository.findByEmailContaining(email);
    }

    //주소 접두사/접두어 회원 목록 조회
    public List<Member> findByAddressMember(String address){
        return memberRepository.findByAddressContaining(address);
    }
    

}
