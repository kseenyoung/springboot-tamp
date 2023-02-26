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
            //암호화
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
}
