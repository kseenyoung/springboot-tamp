package com.example.demo.src.account.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Account {
    private int accountId;
    private String accountEmail;
    private String accountPassword;
    private int membershipId;
}
