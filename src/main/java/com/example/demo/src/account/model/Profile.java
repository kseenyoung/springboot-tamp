package com.example.demo.src.account.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Profile {
    private int accountId;
    private String ageLimit;
    private String language;
    private int lockStatus;
    private int profileId;
    private String status;
    private String profileImageUrl;
    private int profileName;
    private String profilePassword;
}