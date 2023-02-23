package com.example.demo.src.account;



import com.example.demo.config.BaseException;
import com.example.demo.src.account.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

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
    // public PostAccountRes createAccount(PostAccountReq postAccountReq) throws BaseException {
    //     //중복
    //     if(accountProvider.checkEmail(postAccountReq.getEmail()) ==1){
    //         throw new BaseException(POST_USERS_EXISTS_EMAIL);
    //     }

    //     String pwd;
    //     try{
    //         //암호화
    //         pwd = new SHA256().encrypt(postAccountReq.getPassword());
    //         postAccountReq.setPassword(pwd);

    //     } catch (Exception ignored) {
    //         throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
    //     }
    //     try{
    //         int accountIdx = accountDao.createAccount(postAccountReq);
    //         //jwt 발급.
    //         String jwt = jwtService.createJwt(accountIdx);
    //         return new PostAccountRes(jwt,accountIdx);
    //     } catch (Exception exception) {
    //         logger.error("App - createAccount Service Error", exception);
    //         throw new BaseException(DATABASE_ERROR);
    //     }
    // }

    // public void modifyAccountName(PatchAccountReq patchAccountReq) throws BaseException {
    //     try{
    //         int result = accountDao.modifyAccountName(patchAccountReq);
    //         if(result == 0){
    //             throw new BaseException(MODIFY_FAIL_USERNAME);
    //         }
    //     } catch(Exception exception){
    //         logger.error("App - modifyAccountName Service Error", exception);
    //         throw new BaseException(DATABASE_ERROR);
    //     }
    // }
}
