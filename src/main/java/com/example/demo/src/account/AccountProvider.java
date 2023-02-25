package com.example.demo.src.account;


import com.example.demo.config.BaseException;
import com.example.demo.src.account.model.*;
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
public class AccountProvider {

    private final AccountDao accountDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public AccountProvider(AccountDao accountDao, JwtService jwtService) {
        this.accountDao = accountDao;
        this.jwtService = jwtService;
    }

   public List<GetAccountRes> getAccounts() throws BaseException{
       try{
           List<GetAccountRes> getAccountRes = accountDao.getAccounts();
           return getAccountRes;
       }
       catch (Exception exception) {
           logger.error("App - getAccountRes Provider Error", exception);
           throw new BaseException(DATABASE_ERROR);
       }
   }

    public GetAccountRes getAccountsByAccountInfo(String accountId, String accountEmail) throws BaseException {
        try {
            GetAccountRes getAccountsRes = accountDao.getAccountsByAccountInfo(accountId, accountEmail);
            return getAccountsRes;
        } catch (Exception exception) {
            logger.error("App - getAccountsByEmail Provider Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }


//    public GetAccountRes getAccount(int AccountIdx) throws BaseException {
//        try {
//            GetAccountRes getAccountRes = AccountDao.getAccount(AccountIdx);
//            return getAccountRes;
//        } catch (Exception exception) {
//            logger.error("App - getAccount Provider Error", exception);
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }

   public int checkEmail(String email) throws BaseException{
       try{
           return accountDao.checkEmail(email);
       } catch (Exception exception){
           logger.error("App - checkEmail Provider Error", exception);
           throw new BaseException(DATABASE_ERROR);
       }
   }

   public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException {
       try {
    	Account account = accountDao.getPwd(postLoginReq); //이메일로 계정정보 가져옴
		if(account.getAccountEmail() == null) throw new BaseException(FAILED_TO_LOGIN); //계정정보 없으면
           String encryptPwd;
           try {
               encryptPwd = SHA256.encrypt(postLoginReq.getAccountPassword()); //비밀번호 암호화
           } catch (Exception exception) {
               logger.error("App - logIn Provider Encrypt Error", exception);
               throw new BaseException(PASSWORD_DECRYPTION_ERROR);
           }

		//가져온 계정정보와 입력받은 암호비교
		if(!account.getAccountPassword().equals(encryptPwd)) throw new BaseException(FAILED_TO_LOGIN);
		//가입은했지만 결제정보가 없을때
		if(account.getMembershipId() == 0) throw new BaseException(FAILED_TO_LOGIN);

		int AccountIdx = account.getAccountId();
		String jwt = jwtService.createJwt(AccountIdx);
		
		return new PostLoginRes(AccountIdx,jwt);
       }catch(EmptyResultDataAccessException empex){
		logger.error("조회된 아이디없음", empex);
		throw new BaseException(FAILED_TO_LOGIN);
	   }
	   catch (Exception exception) {
           logger.error("App - logIn Provider Error", exception);
           throw new BaseException(DATABASE_ERROR);
       }
   }
}
