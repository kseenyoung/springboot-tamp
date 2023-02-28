package com.example.demo.src.account.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Devices {
    private int deviceId;
    private int accountId;
    private int profileId;
    private String deviceName;
    private String ipAddress;
    private String status;
    private String loginTime;
}