package org.myteam.server.admin.service;


import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.dto.AdminSearch;
import org.myteam.server.admin.dto.AdminUpdateDto;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.news.dto.repository.NewsCommentMemberDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static org.myteam.server.admin.dto.AdminSearch.*;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final PasswordEncoder passwordEncoder;
    private final SecurityReadService securityReadService;
    private final MemberJpaRepository memberJpaRepository;

    public String updateAdmin(AdminUpdateDto adminUpdateDto){


        Member member=securityReadService.getMember();

        boolean changeCheck=false;

        if(!adminUpdateDto.getPassword().isEmpty()&!passwordEncoder.matches(adminUpdateDto.getPassword(),member.getPassword())){

            member.updatePassword(passwordEncoder.encode(adminUpdateDto.getPassword()));
            changeCheck=true;
        }
        if(!member.getNickname().equals(adminUpdateDto.getNickName())){

            member.updateNickName(adminUpdateDto.getNickName());
            changeCheck=true;
        }
        if(!member.getImgUrl().equals(adminUpdateDto.getImgUrl())){
            member.updateImgUrl(adminUpdateDto.getImgUrl());
            changeCheck=true;
        }

        if(changeCheck){

            memberJpaRepository.save(member);
        }

        return "ok";
    }


    public ResponseAdminProfile getAdminProfile(){


        Member member=securityReadService.getMember();
        return  ResponseAdminProfile.builder()
                .imgUrl(member.getImgUrl())
                .email(member.getEmail())
                .nickName(member.getNickname())
                .build();






    }


}
