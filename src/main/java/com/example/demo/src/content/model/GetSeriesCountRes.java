package com.example.demo.src.content.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetSeriesCountRes {
    private String mainTitle;
    private String contentType;
    private int season;
    private int seriesCount;
};
