package com.example.demo.src.content;



import com.example.demo.config.BaseException;
import com.example.demo.src.content.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class ContentService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ContentDao contentDao;
    private final ContentProvider contentProvider;
    private final JwtService jwtService;


    @Autowired
    public ContentService(ContentDao contentDao, ContentProvider contentProvider, JwtService jwtService) {
        this.contentDao = contentDao;
        this.contentProvider = contentProvider;
        this.jwtService = jwtService;

    }

}
