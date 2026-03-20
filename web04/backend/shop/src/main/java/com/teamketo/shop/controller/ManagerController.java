package com.teamketo.shop.controller;

import com.teamketo.shop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//중간(부서별) 관리자 기능
@RestController
@RequiredArgsConstructor
@RequestMapping("/manager")
public class ManagerController {

    private final MemberService memberService;

    //일반 회원 목록
    
    //특정 회원 정보 조회
}
