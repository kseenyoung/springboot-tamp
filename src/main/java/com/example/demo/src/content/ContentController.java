package com.example.demo.src.content;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.content.model.*;
import com.example.demo.utils.JwtService;

import net.bytebuddy.dynamic.loading.PackageDefinitionStrategy.Definition.Undefined;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

@RestController
@RequestMapping("/contents")
public class ContentController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ContentProvider contentProvider;
    @Autowired
    private final ContentService contentService;
    @Autowired
    private final JwtService jwtService;

    //생성자 : Constructor
    public ContentController(ContentProvider contentProvider, ContentService contentService, JwtService jwtService){
        this.contentProvider = contentProvider;
        this.contentService = contentService;
        this.jwtService = jwtService;
    }

        /**
     * 모든 영상 조회 API
     * [GET] /contents
     * 회원 번호 검색 조회 API
     * [GET] /contents/? contentId=
     * @return BaseResponse<List<GetContentRes>>
     */
    //Query String
    @ResponseBody
    @GetMapping("") // (GET) 127.0.0.1:9000/contents
    public BaseResponse<List<GetContentRes>> getContents(@RequestParam(required = false) String contentId, //아이디로 조회
														 @RequestParam(required = false) String maintitle //제목으로 조회
														 ) {
        try{
            if(contentId != null || maintitle != null){
				List<GetContentRes> getContentsRes = new ArrayList<GetContentRes>();
				GetContentRes getContent = contentProvider.getContentsByContentInfo(contentId, maintitle);
				getContentsRes.add(getContent);
				return new BaseResponse<>(getContentsRes);
			}
            // Get Contents - 전체 계정 조회
			List<GetContentRes> getContentsRes = contentProvider.getContents();
            return new BaseResponse<>(getContentsRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    	/**
	* 계정 탈퇴 API
	* [PATCH] /contents/:contentId/status
	* @return BaseResponse<String>
    	 * @throws BaseException
	*/
	@ResponseBody
	@PatchMapping("/{contentId}/status")
	public BaseResponse<String> modifyContentStatus(@PathVariable("contentId") int contentId) throws BaseException{
        // status 상태 변경
        PatchContentReq patchContentReq = new PatchContentReq(contentId, "DEACTIVE");
        contentService.modifyContentStatus(patchContentReq);

        String result = String.format("contentId %d is successfully deactive", contentId);
        return new BaseResponse<>(result);
	}


    //     /**
    //  * 시리즈 별 에피소드 개수 조회 API
    //  * [GET] /contents/:contentId/episodes
    //  * @return BaseResponse<List<GetContentRes>>
    //  */
    // //Query String
    // @ResponseBody
    // @GetMapping("/{contentId}/episodes") // (GET) 127.0.0.1:9000/contents/:contentId/episodes
    // public BaseResponse<List<GetSeriesCountRes>> getSeriesCount(@PathVariable("contentId") int contentId) throws BaseException{
	// 			List<GetSeriesCountRes> getSeriesCountRes = contentProvider.getSeriesCountByContentId(contentId);
	// 			return new BaseResponse<>(getSeriesCountRes);
	// 		}
    //     }



}

