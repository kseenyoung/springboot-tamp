package com.example.demo.src.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.account.model.*;
import com.example.demo.utils.JwtService;

import net.bytebuddy.dynamic.loading.PackageDefinitionStrategy.Definition.Undefined;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final AccountProvider accountProvider;
    @Autowired
    private final AccountService accountService;
    @Autowired
    private final JwtService jwtService;

    //생성자 : Constructor
    public AccountController(AccountProvider accountProvider, AccountService accountService, JwtService jwtService){
        this.accountProvider = accountProvider;
        this.accountService = accountService;
        this.jwtService = jwtService;
    }

    /**
     * 특정 계정 조회 API
     * [GET] /accounts
     * 회원 번호 검색 조회 API
     * [GET] /accounts/? accountId=
     * @return BaseResponse<List<GetAccountRes>>
     */
    //Query String
    @ResponseBody
    @GetMapping("") // (GET) 127.0.0.1:9000/accounts
    public BaseResponse<List<GetAccountRes>> getAccounts(@RequestParam(required = false) String accountId, //아이디로 조회
														 @RequestParam(required = false) String accountEmail //이메일로 조회
														 ) {
        try{
            if(accountId != null || accountEmail != null){
				List<GetAccountRes> getAccountsRes = new ArrayList<GetAccountRes>();
				GetAccountRes getAccount = accountProvider.getAccountsByAccountInfo(accountId, accountEmail);
				getAccountsRes.add(getAccount);
				return new BaseResponse<>(getAccountsRes);
			}
            // Get Accounts - 전체 계정 조회
			List<GetAccountRes> getAccountsRes = accountProvider.getAccounts();
            return new BaseResponse<>(getAccountsRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

//    /**
//     * 유저정보변경 API
//     * [PATCH] /users/:userIdx
//     * @return BaseResponse<String>
//     */
//    @ResponseBody
//    @PatchMapping("/{accountId}")
//    public BaseResponse<String> modifyUserName(@PathVariable("accountId") int accountId, @RequestBody Account account){
//        try {
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getAccountId();
//            //userIdx와 접근한 유저가 같은지 확인
//            if(accountId != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
//            //같다면 유저네임 변경
//            PatchAccountReq patchUserReq = new PatchAccountReq(accountId,account.getAccountName());
//            AccountService.modifyAccountName(patchAccountReq);

//            String result = "";
//            return new BaseResponse<>(result);
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }



//    /**
//     * 회원 1명 조회 API
//     * [GET] /users/:userIdx
//     * @return BaseResponse<GetUserRes>
//     */
//    // Path-variable
//    @ResponseBody
//    @GetMapping("/{userIdx}") // (GET) 127.0.0.1:9000/app/users/:userIdx
//    public BaseResponse<GetUserRes> getUser(@PathVariable("userIdx") int userIdx) {
//        // Get Users
//        try{
//            GetUserRes getUserRes = userProvider.getUser(userIdx);
//            return new BaseResponse<>(getUserRes);
//        } catch(BaseException exception){
//            return new BaseResponse<>((exception.getStatus()));
//        }
//
//    }

   /**
    * 회원가입 API
    * [POST] /accounts
    * @return BaseResponse<PostUserRes>
    */
   // Body
   @ResponseBody
   @PostMapping("/join")
   public BaseResponse<PostLoginRes> createAccount(@RequestBody PostAccountReq postAccountReq) {
       // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!
       if(postAccountReq.getAccountEmail() == null){
           return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
       }
       //이메일 정규표현
       if(!isRegexEmail(postAccountReq.getAccountEmail())){
           return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
       }
       try{
        PostLoginRes postLoginRes = accountService.createAccount(postAccountReq);
           return new BaseResponse<>(postLoginRes);
       } catch(BaseException exception){
           return new BaseResponse<>((exception.getStatus()));
       }
   }

   /**
    * 로그인 API
    * [POST] /users/logIn
    * @return BaseResponse<PostLoginRes>
    */
   @ResponseBody
   @PostMapping("/login")
   public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq){
       try{
           PostLoginRes postLoginRes = accountProvider.logIn(postLoginReq);
           if(!loginValidChk(postLoginReq)) return new BaseResponse<>(USERS_EXFIRED);
           return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
    
    public boolean loginValidChk(PostLoginReq postLoginReq){
       // TODO: 로그인 값들에 대한 형식적인 validatin 처리해주셔야합니다!
       // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.
    return true;
   }

	/**
	* 계정 멤버쉽 변경 API
	* [PATCH] /account/:accountId/memberships
	* @return BaseResponse<String>
	*/
	@ResponseBody
	@PatchMapping("/{accountId}/memberships")
	public BaseResponse<String> modifyAccountMemberships(@PathVariable("accountId") int accountId, @RequestBody PatchAccountReq user){
		try {
			//jwt에서 idx 추출.
			int userIdxByJwt = jwtService.getUserIdx();
			//userIdx와 접근한 유저가 같은지 확인
			if(accountId != userIdxByJwt){
				return new BaseResponse<>(INVALID_USER_JWT);
			}
			//같다면 유저네임 변경
			PatchAccountReq patchUserReq = new PatchAccountReq(accountId, "****", user.getMembershipId());
			accountService.modifyAccountMemberships(patchUserReq);

			String result = "";
			return new BaseResponse<>(result);
		} catch (BaseException exception) {
			return new BaseResponse<>((exception.getStatus()));
		}
	}

	/**
	* 계정 비밀번호 변경 API
	* [PATCH] /account/:accountId/passwords
	* @return BaseResponse<String>
	*/
	@ResponseBody
	@PatchMapping("/{accountId}/passwords")
	public BaseResponse<String> modifyAccountPasswords(@PathVariable("accountId") int accountId, @RequestBody PatchAccountReq user){
		try {
			//jwt에서 idx 추출.
			int userIdxByJwt = jwtService.getUserIdx();
			//userIdx와 접근한 유저가 같은지 확인
			if(accountId != userIdxByJwt){
				return new BaseResponse<>(INVALID_USER_JWT);
			}
			//같다면 유저네임 변경
			PatchAccountReq patchUserReq = new PatchAccountReq(accountId, "****", user.getMembershipId());
			accountService.modifyAccountPasswords(patchUserReq);

			String result = "";
			return new BaseResponse<>(result);
		} catch (BaseException exception) {
			return new BaseResponse<>((exception.getStatus()));
		}
	}


	// /**
	// * 계정 탈퇴 API
	// * [PATCH] /account/:accountId/status
	// * @return BaseResponse<String>
	// */
	// @ResponseBody
	// @PatchMapping("/{accountId}/status")
	// public BaseResponse<String> modifyAccountStatus(@PathVariable("accountId") int accountId, @RequestBody PostLoginReq postLoginReq){
	// 	try {
	// 		//jwt에서 idx 추출.
	// 		int userIdxByJwt = jwtService.getUserIdx();
	// 		//userIdx와 접근한 유저가 같은지 확인
	// 		if(accountId != userIdxByJwt){
	// 			return new BaseResponse<>(INVALID_USER_JWT);
	// 		}
	// 		//같다면 유저네임 변경
	// 		PatchAccountReq patchUserReq = new PatchAccountReq(accountId, "****", user.getMembershipId());
	// 		accountService.modifyAccountPasswords(patchUserReq);

	// 		String result = "";
	// 		return new BaseResponse<>(result);
	// 	} catch (BaseException exception) {
	// 		return new BaseResponse<>((exception.getStatus()));
	// 	}
	// }

}
