package com.example.demo.src.content.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetSeriesCountReq {
    private int contentId;
    private String mainTitle;
    private int season;
}
