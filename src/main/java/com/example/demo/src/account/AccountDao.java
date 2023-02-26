package com.example.demo.src.account;


import com.example.demo.src.account.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class AccountDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

   public List<GetAccountRes> getAccounts(){
       String getAccountsQuery = "select * from Account";
       return this.jdbcTemplate.query(getAccountsQuery,
               (rs,rowNum) -> new GetAccountRes(
				rs.getInt("accountId"),
				rs.getString("accountEmail"),
				rs.getString("accountPassword"),
				rs.getInt("membershipId"),
				rs.getString("accountStatus"),
				rs.getString("status"),
				rs.getString("telephoneNumber"))
       		);
   }

	public List<Profile> getProfiles(int accountId){
		String getAccountsQuery = "select * from Profile where accountId = ?";
		return this.jdbcTemplate.query(getAccountsQuery,
				(rs,rowNum) -> new Profile(
				rs.getInt("accountId"),
				rs.getString("ageLimit"),
				rs.getString("language"),
				rs.getString("lockStatus"),
				rs.getInt("profileId"),
				rs.getString("status"),
				rs.getString("profileImageUrl"),
				rs.getString("profileName"),
				rs.getInt("profilePassword")),
				accountId
			);
	}

    public GetAccountRes getAccountsByAccountInfo(String accountId, String accountEmail){

        String getAccountQuery = "";
		Object getAccountsByAccountParam = null;

		if(accountId != null){
			getAccountQuery = "select * from Account where accountId = ?";
			getAccountsByAccountParam = Integer.parseInt(accountId);
		}else{
			getAccountQuery = "select * from Account where accountEmail = ?";
			getAccountsByAccountParam = accountEmail;
		}

        return this.jdbcTemplate.queryForObject(getAccountQuery,
                (rs, rowNum) -> new GetAccountRes(
						rs.getInt("accountId"),
						rs.getString("accountEmail"),
						rs.getString("accountPassword"),
						rs.getInt("membershipId"),
						rs.getString("accountStatus"),
						rs.getString("status"),
						rs.getString("telephoneNumber")),
					getAccountsByAccountParam);
    }

	public int createAccount(PostAccountReq postAccountReq){
		String createAccountQuery = "insert into Account (accountEmail, accountPassword, telephoneNumber, membershipId, AccountStatus) VALUES (?,?,?,?,?)";
		Object[] createAccountParams = 
		new Object[]{
			postAccountReq.getAccountEmail(), 
			postAccountReq.getAccountPassword(), 
			postAccountReq.getTelephoneNumber(), 
			postAccountReq.getMembershipId(), 
			"ACTIVE"};

		this.jdbcTemplate.update(createAccountQuery, createAccountParams);

		String lastInserIdQuery = "select last_insert_id()";
		return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
	}

   public int checkEmail(String email){
       String checkEmailQuery = "select exists(select accountEmail from Account where accountEmail = ?)";
       String checkEmailParams = email;
       return this.jdbcTemplate.queryForObject(checkEmailQuery,
               int.class,
               checkEmailParams);
   }

   public int modifyAccountMemberships(PatchAccountReq patchAccountReq){
       String modifyAccountNameQuery = "update Account set membershipId = ? where accountId = ? ";
       Object[] modifyAccountNameParams = new Object[]{patchAccountReq.getMembershipId(), patchAccountReq.getAccountId()};

       return this.jdbcTemplate.update(modifyAccountNameQuery,modifyAccountNameParams);
   }

   public int modifyAccountPasswords(PatchAccountReq patchAccountReq){
       String modifyAccountNameQuery = "update Account set accountPassword = ? where accountId = ? ";
       Object[] modifyAccountNameParams = new Object[]{patchAccountReq.getAccountPassword(), patchAccountReq.getAccountId()};

       return this.jdbcTemplate.update(modifyAccountNameQuery,modifyAccountNameParams);
   }

   public int modifyAccountStatus(Account account){
       String modifyAccountNameQuery = "update Account set accountStatus = ? ,status = ? where accountEmail = ? and accountPassword = ? and accountId = ? ";
       Object[] modifyAccountNameParams = new Object[]{"DELETE","DELETE", account.getAccountEmail(), account.getAccountPassword(), account.getAccountId()};

       return this.jdbcTemplate.update(modifyAccountNameQuery,modifyAccountNameParams);
   }

	public Account getPwd(PostLoginReq postLoginReq){
		String getPwdQuery = "select accountId, accountEmail, accountPassword, membershipId, accountStatus, status, telephoneNumber from Account where accountEmail = ?";
		String getPwdParams = postLoginReq.getAccountEmail();

		return this.jdbcTemplate.queryForObject(getPwdQuery,
				(rs,rowNum)-> new Account(
				rs.getInt("accountId"),
				rs.getString("accountEmail"),
				rs.getString("accountPassword"),
				rs.getInt("membershipId"),
				rs.getString("accountStatus"),
				rs.getString("status"),
				rs.getString("telephoneNumber")),
				getPwdParams
		);
	}

   public int appendProfile(Profile profile){
	String createAccountQuery = "insert into Profile (accountId, ageLimit, profileImageUrl, profileName, profilePassword) VALUES (?,?,?,?,?)";
	Object[] createAccountParams = 
	new Object[]{
		profile.getAccountId(), 
		profile.getAgeLimit(), 
		profile.getProfileImageUrl(), 
		profile.getProfileName(), 
		profile.getProfilePassword(), 
		};

	this.jdbcTemplate.update(createAccountQuery, createAccountParams);

	String lastInserIdQuery = "select last_insert_id()";
	return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
	}

   public int appendPayment(Payment payment){
	String createAccountQuery = "insert into PaymentCard (accountId, paymentCardType, standardCard, cardNumber, status) VALUES (?,?,?,?,?)";
	Object[] createAccountParams = 
	new Object[]{
		payment.getAccountId(), 
		payment.getPaymentCardType(), 
		payment.getStandardCard(),
		payment.getCardNumber(),
		"ACTIVE"
		};

	this.jdbcTemplate.update(createAccountQuery, createAccountParams);

	String lastInserIdQuery = "select last_insert_id()";
	return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
	}

	public List<Payment> getPayments(int accountId){
		String getAccountsQuery = "select * from PaymentCard where accountId = ?";
		return this.jdbcTemplate.query(getAccountsQuery,
				(rs,rowNum) -> new Payment(
				rs.getInt("accountId"),
				rs.getInt("paymentCardId"),
				rs.getString("paymentCardType"),
				rs.getString("standardCard"),
				rs.getString("cardNumber"),
				rs.getString("status")),
				accountId
			);
	}
}
