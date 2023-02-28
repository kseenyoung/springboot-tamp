package com.example.demo.src.socialLogin.model;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Getter
@Setter
public class SocialAccount {
    private int accountId;
    private String accountPassword;
    private String accountEmail;
}
