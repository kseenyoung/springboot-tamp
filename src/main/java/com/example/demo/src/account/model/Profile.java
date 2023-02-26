package com.example.demo.src.account.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    private int accountId;
    private String ageLimit;
    private String language;
    private String lockStatus;
    private int profileId;
    private String status;
    private String profileImageUrl;
    private String profileName;
    private int profilePassword;
}