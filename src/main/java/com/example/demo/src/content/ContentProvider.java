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


    public GetSeriesCountRes getSeriesCountByContentInfo(String maintitle, int season) throws BaseException {
        try{
            GetSeriesCountRes getSeriesCountRes = contentDao.getSeriesCountByContentInfo(maintitle, season);
            return getSeriesCountRes;
        }
        catch (Exception exception){
            logger.error("App - getSeriesCountByContentInfo Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void postContentsByContentInfo(Content req) throws BaseException {
        try {
            Content content = new Content(
                0, //auto_increment
                req.getExplanation(),
                req.getContentType(),
                req.getAgeLimitCode(), //19+, 청불, 12+, 7+, 전체관람가
                req.getCreationNational(),
                req.getCreationDate(),
                req.getRunningTime(),
                req.getMainTitle(),
                req.getContentUrl(),
                req.getSeason()
            );

            int result = contentDao.getContentsByContentInfo(content);
            if(result == 0) throw new BaseException(APPEND_FAIL_CONTENT);

        } catch (Exception exception) {
            logger.error("App - getContentsByMainTitle Provider Error", exception);
            throw new BaseException(DATABASE_ZERO_ACTUAL);
        }
    }

}
