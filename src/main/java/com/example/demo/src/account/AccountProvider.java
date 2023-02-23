package com.example.demo.src.account;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.account.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

//    public List<GetAccountRes> getAccounts() throws BaseException{
//        try{
//            List<GetAccountRes> getAccountRes = accountDao.getAccounts();
//            return getAccountRes;
//        }
//        catch (Exception exception) {
//            logger.error("App - getAccountRes Provider Error", exception);
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }

    public List<GetAccountRes> getAccountsByAccountId(int accountId) throws BaseException {
        try {
            List<GetAccountRes> getAccountsRes = accountDao.getAccountsByAccountId(accountId);
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
//
//    public int checkEmail(String email) throws BaseException{
//        try{
//            return AccountDao.checkEmail(email);
//        } catch (Exception exception){
//            logger.error("App - checkEmail Provider Error", exception);
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException {
//        try {
//            Account Account = AccountDao.getPwd(postLoginReq);
//
//            String encryptPwd;
//            try {
//                encryptPwd = new SHA256().encrypt(postLoginReq.getPassword());
//            } catch (Exception exception) {
//                logger.error("App - logIn Provider Encrypt Error", exception);
//                throw new BaseException(PASSWORD_DECRYPTION_ERROR);
//            }
//
//            if(Account.getPassword().equals(encryptPwd)){
//                int AccountIdx = Account.getAccountIdx();
//                String jwt = jwtService.createJwt(AccountIdx);
//                return new PostLoginRes(AccountIdx,jwt);
//            }
//            else{
//                throw new BaseException(FAILED_TO_LOGIN);
//            }
//        } catch (Exception exception) {
//            logger.error("App - logIn Provider Error", exception);
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
}
