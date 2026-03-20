package com.teamketo.shop.repository;

import com.teamketo.shop.entity.Member;
import com.teamketo.shop.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    //회원 가입(save)
    //회원 목록(findAll)
    //특정 회원 정보 조회(findById)
    //회원 정보 수정(findById, Save)
    //회원 탈퇴(deleteById)

    //이메일로 회원 정보 조회
    Optional<Member> findByEmail(String email);
    
    //이메일로 중복 확인
    boolean existsByEmail(String email);
    
    //권한별 조회
    List<Member> findByRole(Role role);
    //스프링부트 내부에서 판별할 때: ROLE_ADMIN, ROLE_MANAGER, ROLE_USER

    //주소 조회
    List<Member> findByAddress(String address);

    //이메일별 회원 목록 조회
    List<Member> findByEmailContaining(String email);

    //주소 접두사/접두어 회원 목록 조회
    List<Member> findByAddressContaining(String address);
}
