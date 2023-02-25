package com.example.demo.src.account.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetAccountRes {
    private int accountId;
    private String accountEmail;
    private String accountPassword;
    private int membershipId;
    private String accountStatus;
    private String status;
    private String telephoneNumber;
}
