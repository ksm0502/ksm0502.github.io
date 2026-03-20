package com.teamketo.shop.dto;

import com.teamketo.shop.entity.Member;
import com.teamketo.shop.entity.Role;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MemberResponseDto {

    private Long id;
    private String email;
    private String name;
    private String phone;
    private String address;
    private Role role;
    private LocalDateTime createdAt;

    public MemberResponseDto(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.name = member.getName();
        this.phone = member.getPhone();
        this.address = member.getAddress();
        this.role = member.getRole();
        this.createdAt = member.getCreatedAt();
    }
}
