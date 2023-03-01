package com.example.demo.src.account;



import com.example.demo.config.BaseException;
import com.example.demo.src.account.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

// Service Create, Update, Delete 의 로직 처리
@Service
public class AccountService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AccountDao accountDao;
    private final AccountProvider accountProvider;
    private final JwtService jwtService;


    @Autowired
    public AccountService(AccountDao accountDao, AccountProvider accountProvider, JwtService jwtService) {
        this.accountDao = accountDao;
        this.accountProvider = accountProvider;
        this.jwtService = jwtService;

    }

    // //POST
    public PostLoginRes createAccount(PostAccountReq postAccountReq) throws BaseException {
        //중복
        if(accountProvider.checkEmail(postAccountReq.getAccountEmail()) == 1){
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }

        String pwd;
        try{
            //암호화
            pwd = SHA256.encrypt(postAccountReq.getAccountPassword());
            postAccountReq.setAccountPassword(pwd);

        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        try{
            int accountIdx = accountDao.createAccount(postAccountReq);
            //jwt 발급.
            String jwt = jwtService.createJwt(accountIdx);
            return new PostLoginRes(accountIdx, jwt);
        } catch (Exception exception) {
            logger.error("App - createAccount Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public static String getKaKaoAccessToken(String code){
        String access_Token="";
        String refresh_Token ="";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try{
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=9cefa6a9dfbb156f28ccd0a7b1adbd0a"); // TODO REST_API_KEY 입력
            sb.append("&redirect_uri=http://localhost:9000/accounts/oauth"); // TODO 인가코드 받은 redirect_uri 입력
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);
            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access_token : " + access_Token);
            System.out.println("refresh_token : " + refresh_Token);

            br.close();
            bw.close();
        }catch (IOException e) {
            e.printStackTrace();
        }

        return access_Token;
    }


    public static void createKakaoUser(String token) throws BaseException {

        String reqURL = "https://kapi.kakao.com/v2/user/me";

        //access_token을 이용하여 사용자 정보 조회
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + token); //전송할 header 작성, access_token전송

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리로 JSON파싱
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            Long id = element.getAsJsonObject().get("id").getAsLong();
            boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
            String email = "";
            if (hasEmail) {
                email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            }

            System.out.println("id : " + id);
            System.out.println("email : " + email);

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void modifyAccountMemberships(PatchAccountReq patchAccountReq) throws BaseException {
        try{
            int result = accountDao.modifyAccountMemberships(patchAccountReq);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_USERNAME);
            }
        } catch(Exception exception){
            logger.error("App - modifyAccountName Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyAccountPasswords(PatchAccountReq patchAccountReq) throws BaseException {
        try{
            //암호화
            String pwd = SHA256.encrypt(patchAccountReq.getAccountPassword());
            patchAccountReq.setAccountPassword(pwd);

            int result = accountDao.modifyAccountPasswords(patchAccountReq);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_USERNAME);
            }
        } catch(Exception exception){
            logger.error("App - modifyAccountName Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyAccountStatus(Account account) throws BaseException {
        try{
            // 암호화
            String pwd = SHA256.encrypt(account.getAccountPassword());
            account.setAccountPassword(pwd);

            int result = accountDao.modifyAccountStatus(account);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_USERNAME);
            }
        } catch(Exception exception){
            logger.error("App - modifyAccountName Service Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

	public void appendProfile(int accountId, Profile req) throws BaseException {
		try{
			Profile profile = new Profile(
				accountId,
				req.getAgeLimit(), //19+, 청불, 12+, 7+, 전체관람가
				req.getLanguage(), //KO, EN, ...
				req.getLockStatus(), //ACTIVE, DISABLED
				0, //auto_increment
				"ACTIVE", 
				req.getProfileImageUrl() == null ? "https://occ-0-993-2218.1.nflxso.net/dnm/api/v6/K6hjPJd6cR6FpVELC5Pd6ovHRSk/AAAABULvkIoX3Lsmb1j9ZGivfBory3qQ5p32A0lWmVmbGm2v2zJdJTNE3XCnqRbKOXhjcJsQijTuPlPo5QDZXjto885KBMbGWkheqg.png?r=1d4" : req.getProfileImageUrl(), 
				req.getProfileName(),
				req.getProfilePassword()
			);
			
			int result = accountDao.appendProfile(profile);
			if(result == 0) throw new BaseException(APPEND_FAIL_PROFILE);
			
		} catch(Exception exception){
			logger.error("App - appendProfile Service Error", exception);
			throw new BaseException(DATABASE_ERROR);
		}
	}

	public void appendPayment(int accountId, Payment req) throws BaseException {
		try{
			Payment profile = new Payment();
			profile.setAccountId(accountId);
			profile.setCardNumber(req.getCardNumber());
			profile.setPaymentCardType(req.getPaymentCardType());
			profile.setStandardCard(req.getStandardCard());

			int result = accountDao.appendPayment(profile);
			if(result == 0) throw new BaseException(APPEND_FAIL_PROFILE);
			
		} catch(Exception exception){
			logger.error("App - appendPayment Service Error", exception);
			throw new BaseException(DATABASE_ERROR);
		}
	}

	public void appendDevices(int accountId, int profileId, Devices req) throws BaseException {
		try{
			Devices devices = new Devices();
			devices.setAccountId(accountId);
			devices.setProfileId(profileId);
			devices.setDeviceName(req.getDeviceName());
			devices.setIpAddress(req.getIpAddress());

			int result = accountDao.appendDevices(devices);
			if(result == 0) throw new BaseException(APPEND_FAIL_PROFILE);
			
		} catch(Exception exception){
			logger.error("App - appendDevices Service Error", exception);
			throw new BaseException(DATABASE_ERROR);
		}
	}

	public void deactivateDevice(int accountId, int deviceId, Devices req) throws BaseException {
		try{
			Devices devices = new Devices();
			devices.setAccountId(accountId);
			devices.setDeviceId(deviceId);

			int result = accountDao.deactivateDevice(devices);
			if(result == 0) throw new BaseException(APPEND_FAIL_PROFILE);
			
		} catch(Exception exception){
			logger.error("App - deactivateDevice Service Error", exception);
			throw new BaseException(DATABASE_ERROR);
		}
	}

	public void updateDevice(int accountId, int deviceId, Devices req) throws BaseException {
		try{
			Devices devices = new Devices();
			devices.setAccountId(accountId);
			devices.setDeviceId(deviceId);
			devices.setIpAddress(req.getIpAddress());

			int result = accountDao.updateDevice(devices);
			if(result == 0) throw new BaseException(APPEND_FAIL_PROFILE);
			
		} catch(Exception exception){
			logger.error("App - deactivateDevice Service Error", exception);
			throw new BaseException(DATABASE_ERROR);
		}
	}
}
