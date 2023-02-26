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
private String deviceId;
private String profileId;
private String deviceName;
private String loginTime;
private String ipAddress;
private String status;
}