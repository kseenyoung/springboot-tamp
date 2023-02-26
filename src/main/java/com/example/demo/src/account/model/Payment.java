package com.example.demo.src.account.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private int accountId;
    private int paymentCardId;
    private String paymentCardType;
    private String standardCard;
    private String status;
    private String cardNumber;
}
