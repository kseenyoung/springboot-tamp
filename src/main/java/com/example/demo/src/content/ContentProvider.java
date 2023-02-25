package com.example.demo.src.content;


import com.example.demo.config.BaseException;
import com.example.demo.src.content.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class ContentProvider {

    private final ContentDao contentDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public ContentProvider(ContentDao contentDao, JwtService jwtService) {
        this.contentDao = contentDao;
        this.jwtService = jwtService;
    }

}
