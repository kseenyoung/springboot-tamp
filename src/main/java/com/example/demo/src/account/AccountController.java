package com.example.demo.src.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.account.model.*;
import com.example.demo.utils.JwtService;

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
			if(accountId != userIdxByJwt) return new BaseResponse<>(INVALID_USER_JWT);
			
			PatchAccountReq patchUserReq = new PatchAccountReq(accountId, "****", user.getMembershipId()); //멤버쉽 변경용 reqVO필요
			accountService.modifyAccountMemberships(patchUserReq);

			String result = "멤버십이 성공적으로 변경되었습니다";
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
			if(accountId != userIdxByJwt) return new BaseResponse<>(INVALID_USER_JWT);

			PatchAccountReq patchUserReq = new PatchAccountReq(accountId, user.getAccountPassword(), user.getMembershipId()); //비밀번호 변경용 reqVO필요
			accountService.modifyAccountPasswords(patchUserReq);

			String result = "비밀번호가 성공적으로 변경되었습니다";
			return new BaseResponse<>(result);
		} catch (BaseException exception) {
			return new BaseResponse<>((exception.getStatus()));
		}
	}


	/**
	* 계정 탈퇴 API
	* [POST] /accounts/:accountId/deactivate
	* @return BaseResponse<String>
	*/
	@ResponseBody
	@PatchMapping("/{accountId}/deactivate")
	public BaseResponse<String> modifyAccountStatus(@PathVariable("accountId") int accountId, @RequestBody Account req){
		try {
			//jwt에서 idx 추출.
			int userIdxByJwt = jwtService.getUserIdx();
			//userIdx와 접근한 유저가 같은지 확인
			if(accountId != userIdxByJwt) return new BaseResponse<>(INVALID_USER_JWT);
			
			Account account = new Account(accountId, req.getAccountEmail(), req.getAccountPassword(), 0, "DELETE","DELETE",null);

			accountService.modifyAccountStatus(account);

			String result = String.format("성공적으로 accountId : %d 를 삭제하였습니다.", accountId);
			return new BaseResponse<>(result);
		} catch (BaseException exception) {
			return new BaseResponse<>((exception.getStatus()));
		}
	}

    /**
     * 특정 계정 프로필 조회 API
     * [GET] /accounts/:accountId/profiles
     * @return BaseResponse<List<GetAccountRes>>
     */
    //Query String
    @ResponseBody
    @GetMapping("/{accountId}/profiles")
    public BaseResponse<List<Profile>> getProfiles(@PathVariable("accountId") int accountId) {
        try {
			//jwt에서 idx 추출.
			int userIdxByJwt = jwtService.getUserIdx();
			//userIdx와 접근한 유저가 같은지 확인
			if(accountId != userIdxByJwt) return new BaseResponse<>(INVALID_USER_JWT);
			
			List<Profile> getAccountsRes = accountProvider.getProfiles(accountId);
            return new BaseResponse<>(getAccountsRes);
		} catch (BaseException exception) {
			return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 특정 계정 프로필 등록 API
     * [POST] /accounts/:accountId/profiles/append
     * @return BaseResponse<List<GetAccountRes>>
     */
    //Query String
    @ResponseBody
    @PostMapping("/{accountId}/profiles/append")
    public BaseResponse<String> appendProfile(@PathVariable("accountId") int accountId, @RequestBody Profile profile) {
        try {
			//jwt에서 idx 추출.
			int userIdxByJwt = jwtService.getUserIdx();
			//userIdx와 접근한 유저가 같은지 확인
			if(accountId != userIdxByJwt) return new BaseResponse<>(INVALID_USER_JWT);
			
			accountService.appendProfile(accountId, profile);

            String result = String.format("accountId : %d , profileName : %s 가 성공적으로 등록 되었습니다.", accountId, profile.getProfileName());
			return new BaseResponse<>(result);
		} catch (BaseException exception) {
			return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 특정 계정 결제정보 등록 API
     * [POST] /accounts/:accountId/payments
     * @return BaseResponse<String>
     */
    //Query String
    @ResponseBody
    @PostMapping("/{accountId}/payments")
    public BaseResponse<String> appendPayment(@PathVariable("accountId") int accountId, @RequestBody Payment payment) {
        try {
			//jwt에서 idx 추출.
			int userIdxByJwt = jwtService.getUserIdx();
			//userIdx와 접근한 유저가 같은지 확인
			if(accountId != userIdxByJwt) return new BaseResponse<>(INVALID_USER_JWT);
			
			accountService.appendPayment(accountId, payment);

            String result = String.format("성공적으로 accountId : %d의 결제 정보 %s 를 추가했습니다.", accountId, payment.getCardNumber());
			return new BaseResponse<>(result);
		} catch (BaseException exception) {
			return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 특정 계정 결제정보 조회 API
     * [GET] /accounts/:accountId/payments
     * @return BaseResponse<String>
     */
    //Query String
    @ResponseBody
    @GetMapping("/{accountId}/payments")
    public BaseResponse<List<Payment>> getPayments(@PathVariable("accountId") int accountId) {
        try {
			//jwt에서 idx 추출.
			int userIdxByJwt = jwtService.getUserIdx();
			//userIdx와 접근한 유저가 같은지 확인
			if(accountId != userIdxByJwt) return new BaseResponse<>(INVALID_USER_JWT);
			
			List<Payment> getPaymentRes = accountProvider.getPayments(accountId);

			return new BaseResponse<>(getPaymentRes);
		} catch (BaseException exception) {
			return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 기기정보 등록 API
     * [POST] /accounts/:accountId/:profileId/devices
     * @return BaseResponse<String>
     */
    //Query String
    @ResponseBody
    @PostMapping("/{accountId}/{profileId}/devices")
    public BaseResponse<String> appendDevices(@PathVariable("accountId") int accountId, @PathVariable("profileId") int profileId, @RequestBody Devices device) {
        try {
			//jwt에서 idx 추출.
			int userIdxByJwt = jwtService.getUserIdx();
			//userIdx와 접근한 유저가 같은지 확인
			if(accountId != userIdxByJwt) return new BaseResponse<>(INVALID_USER_JWT);
			
			accountService.appendDevices(accountId, profileId, device);

            String result = String.format("성공적으로 accountId : %d의 기기 정보 %s 를 추가했습니다.", accountId, device.getDeviceName());
			return new BaseResponse<>(result);
		} catch (BaseException exception) {
			return new BaseResponse<>((exception.getStatus()));
        }
    }

	/**
	 * 기기정보 삭제 API
	 * [POST] /accounts/:accountId/:deviceId/deactivate
	 * @return BaseResponse<String>
	 */
	@ResponseBody
	@PostMapping("/{accountId}/{deviceId}/deactivate")
	public BaseResponse<String> deactivateDevice(@PathVariable("accountId") int accountId, @PathVariable("deviceId") int deviceId, @RequestBody Devices device) {
		try {
			//jwt에서 idx 추출.
			int userIdxByJwt = jwtService.getUserIdx();
			//userIdx와 접근한 유저가 같은지 확인
			if(accountId != userIdxByJwt) return new BaseResponse<>(INVALID_USER_JWT);
			
			accountService.deactivateDevice(accountId, deviceId, device);

			String result = String.format("성공적으로 accountId : %d의 기기 정보 %s 를 삭제했습니다.", accountId, device.getDeviceName());
			return new BaseResponse<>(result);
		} catch (BaseException exception) {
			return new BaseResponse<>((exception.getStatus()));
		}
	}

	/**
	 * 기기정보 갱신 API
	 * [POST] /accounts/:accountId/:deviceId
	 * @return BaseResponse<String>
	 */
	@ResponseBody
	@PatchMapping("/{accountId}/{deviceId}")
	public BaseResponse<String> updateDevice(@PathVariable("accountId") int accountId, @PathVariable("deviceId") int deviceId, @RequestBody Devices device) {
		try {
			//jwt에서 idx 추출.
			int userIdxByJwt = jwtService.getUserIdx();
			//userIdx와 접근한 유저가 같은지 확인
			if(accountId != userIdxByJwt) return new BaseResponse<>(INVALID_USER_JWT);
			
			accountService.updateDevice(accountId, deviceId, device);

			String result = String.format("성공적으로 accountId : %d의 기기 정보 %s 를 수정했습니다.", accountId, device.getDeviceName());
			return new BaseResponse<>(result);
		} catch (BaseException exception) {
			return new BaseResponse<>((exception.getStatus()));
		}
	}

    /**
     * 계정 기기목록 조회 API
     * [GET] /accounts/:accountId/devices
     * @return BaseResponse<String>
     */
    //Query String
    @ResponseBody
    @GetMapping("/{accountId}/devices")
    public BaseResponse<List<Devices>> getDevices(@PathVariable("accountId") int accountId) {
        try {
			//jwt에서 idx 추출.
			int userIdxByJwt = jwtService.getUserIdx();
			//userIdx와 접근한 유저가 같은지 확인
			if(accountId != userIdxByJwt) return new BaseResponse<>(INVALID_USER_JWT);
			
			List<Devices> getDeviceRes = accountProvider.getDevices(accountId);

			return new BaseResponse<>(getDeviceRes);
		} catch (BaseException exception) {
			return new BaseResponse<>((exception.getStatus()));
        }
    }

}