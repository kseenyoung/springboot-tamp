package com.example.demo.src.content.model;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Content {
    private int contentId;
    private String explanation;
    private String contentType;
    private String ageLimitCode;
    private String creationNational;
    private String creationDate;
    private int runningTime;
    private String mainTitle;
    private String contentUrl;
    private int season;
};
