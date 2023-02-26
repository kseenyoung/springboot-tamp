package com.example.demo.src.content.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetSeriesRes {
    private int contentId;
    private String contentType;
    private int season;
    private int seriesCount;
}
