package com.example.demo.src.account.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostAccountRes {
    private String accountEmail;
    private String accountPassword;
    private String telephoneNumber;
}
