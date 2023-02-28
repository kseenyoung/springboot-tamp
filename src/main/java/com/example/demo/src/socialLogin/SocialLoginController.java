package com.example.demo.src.socialLogin;

import com.example.demo.src.socialLogin.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class SocialLoginController {
    private final SocialAccountService socialAccountService;

    @RequestMapping("/oauth")
    public SocialAccount socialLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException{
        return socialAccountService.socialLogin(code, response);
    }
}
