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

    public GetContentRes getContentsByContentInfo(String contentId, String contentMainTitle) throws BaseException {
        try {
            GetContentRes getContentsRes = contentDao.getContentsByContentInfo(contentId, contentMainTitle);
            return getContentsRes;
        } catch (Exception exception) {
            logger.error("App - getContentsByMainTitle Provider Error", exception);
            throw new BaseException(DATABASE_ZERO_ACTUAL);
        }
    }

    public List<GetContentRes> getContents()  throws BaseException{
        try{
            List<GetContentRes> getContentRes = contentDao.getContent();
            return getContentRes;
        }
        catch (Exception exception) {
            logger.error("App - getContentRes Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // public List<GetSeriesCountRes> getSeriesCountByContentId(int contentId) throws BaseException {
    //     try{
    //         List<GetSeriesCountRes> getSeriesCountRes = contentDao.getSeriesCount();
    //         return getSeriesCountRes;
    //     }
    //     catch (Exception exception){
    //         logger.error("App - getContentRes Provider Error", exception);
    //         throw new BaseException(DATABASE_ERROR);
    //     }
    // }

}
