package com.hanghae.greenstep.member;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class MemberController {

    private final MemberService memberService;


    @PatchMapping
    public ResponseEntity<?> updateProfileInfo(@RequestBody MemberRequestDto memberRequestDto,HttpServletRequest request){
        return memberService.updateProfileInfo(memberRequestDto,request);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshTokenCheck(@RequestBody String nickname, HttpServletRequest request, HttpServletResponse response){
        return memberService.refreshToken(nickname, request, response);
    }
}
