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

//    public List<GetAccountRes> getAccounts(){
//        String getAccountsQuery = "select * from AccountInfo";
//        return this.jdbcTemplate.query(getAccountsQuery,
//                (rs,rowNum) -> new GetAccountRes(
//                        rs.getInt("accountIdx"),
//                        rs.getString("accountName"),
//                        rs.getString("ID"),
//                        rs.getString("Email"),
//                        rs.getString("password"))
//        );
//    }

    public List<GetAccountRes> getAccountsByAccountId(int accountId){
        String getAccountsByAccountIdQuery = "select * from Account where accountId =?";
        int getAccountsByAccountIdParams = accountId;
        return this.jdbcTemplate.query(getAccountsByAccountIdQuery,
                (rs, rowNum) -> new GetAccountRes(
                        rs.getInt("accountId"),
                        rs.getString("accountEmail"),
                        rs.getString("accountPassword"),
                        rs.getInt("membershipId")),
                getAccountsByAccountIdParams);
    }

//    public GetAccountRes getAccount(int accountId){
//        String getAccountQuery = "select * from AccountInfo where accountIdx = ?";
//        int getAccountParams = accountIdx;
//        return this.jdbcTemplate.queryForObject(getAccountQuery,
//                (rs, rowNum) -> new GetAccountRes(
//                        rs.getInt("accountIdx"),
//                        rs.getString("accountName"),
//                        rs.getString("ID"),
//                        rs.getString("Email"),
//                        rs.getString("password")),
//                getAccountParams);
//    }
//
//
//    public int createAccount(PostAccountReq postAccountReq){
//        String createAccountQuery = "insert into AccountInfo (accountName, ID, password, email) VALUES (?,?,?,?)";
//        Object[] createAccountParams = new Object[]{postAccountReq.getAccountName(), postAccountReq.getId(), postAccountReq.getPassword(), postAccountReq.getEmail()};
//        this.jdbcTemplate.update(createAccountQuery, createAccountParams);
//
//        String lastInserIdQuery = "select last_insert_id()";
//        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
//    }
//
//    public int checkEmail(String email){
//        String checkEmailQuery = "select exists(select email from AccountInfo where email = ?)";
//        String checkEmailParams = email;
//        return this.jdbcTemplate.queryForObject(checkEmailQuery,
//                int.class,
//                checkEmailParams);
//
//    }
//
//    public int modifyAccountName(PatchAccountReq patchAccountReq){
//        String modifyAccountNameQuery = "update AccountInfo set accountName = ? where accountIdx = ? ";
//        Object[] modifyAccountNameParams = new Object[]{patchAccountReq.getAccountName(), patchAccountReq.getAccountIdx()};
//
//        return this.jdbcTemplate.update(modifyAccountNameQuery,modifyAccountNameParams);
//    }
//
//    public Account getPwd(PostLoginReq postLoginReq){
//        String getPwdQuery = "select accountIdx, password,email,accountName,ID from AccountInfo where ID = ?";
//        String getPwdParams = postLoginReq.getId();
//
//        return this.jdbcTemplate.queryForObject(getPwdQuery,
//                (rs,rowNum)-> new Account(
//                        rs.getInt("accountIdx"),
//                        rs.getString("ID"),
//                        rs.getString("accountName"),
//                        rs.getString("password"),
//                        rs.getString("email")
//                ),
//                getPwdParams
//        );
//
//    }


}
