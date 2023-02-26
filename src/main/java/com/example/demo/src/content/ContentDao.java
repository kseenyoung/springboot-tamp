package com.example.demo.src.content;


import com.example.demo.src.content.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ContentDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetContentRes> getContent() {
        String getContentsQuery = "select * from Content status='ACTIVE'";
        return this.jdbcTemplate.query(getContentsQuery,
                (rs,rowNum) -> new GetContentRes(
                 rs.getInt("contentId"),
                 rs.getString("explanation"),
                 rs.getString("contentType"),
                 rs.getString("ageLimitCode"),
                 rs.getString("creationNational"),
                 rs.getString("creationDate"),
                 rs.getInt("runningTime"),
                 rs.getString("mainTitle"),
                 rs.getString("contentUrl"),
                 rs.getInt("season")
                 )
                );
    }

    public GetContentRes getContentsByContentInfo(String contentId, String contentMainTitle) {
        String getContentQuery = "";
		Object getContentsByContentParam = null;

		if(contentId != null){
			getContentQuery = "select * from Content where contentId = ? and status='ACTIVE'";
			getContentsByContentParam = Integer.parseInt(contentId);
		}else{
			getContentQuery = "select * from Content where mainTitle like '%?%' and status = 'ACTIVE' Group by mainTitle";
			getContentsByContentParam = contentMainTitle;
		}

        return this.jdbcTemplate.queryForObject(getContentQuery,
                (rs, rowNum) -> new GetContentRes(
                    rs.getInt("contentId"),
                    rs.getString("explanation"),
                    rs.getString("contentType"),
                    rs.getString("ageLimitCode"),
                    rs.getString("creationNational"),
                    rs.getString("creationDate"),
                    rs.getInt("runningTime"),
                    rs.getString("mainTitle"),
                    rs.getString("contentUrl"),
                    rs.getInt("season")
                    ),
					getContentsByContentParam);

    }

    public int modifyContentStatus(PatchContentReq patchContentReq) {
        String modifyContentStatusQuery = "update Content set status = ? where contentId = ? ";
        Object[] modifyContentStatusParams = new Object[]{patchContentReq.getStatus(), patchContentReq.getContentId()};
 
        return this.jdbcTemplate.update(modifyContentStatusQuery,modifyContentStatusParams);
    }

    // public List<GetSeriesCountRes> getSeriesCount() {
    //     String getSeriesCountQuery = "SELECT * FROM CONTENT WHERE CONTENTID = ? GROUP BY ";
	// 	Object getSeriesCountByContentParam = null;

    //     return this.jdbcTemplate.queryForObject(getSeriesCountQuery, 
    //             (rs, rowNum) -> new GetSeriesCountRes(
    //                 rs.getInt("contntId"),
    //                 rs.getString("contentType"),
    //                 rs.getInt("season"),
    //                 rs.getInt("seriesCount")
    //             ), getSeriesCountByContentParam);
    // }    }

}
