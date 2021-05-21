package com.vfts.user.service;

import com.vfts.user.dao.IUserMapper;
import com.vfts.user.entity.UserEntity;
import com.vfts.user.entity.QuestionEntity;
import com.vfts.user.iface.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * UserService Implementation class for IUserService
 * @author Axl
 */
@Slf4j
@Service
public class UserService implements IUserService {

    @Autowired
    private IUserMapper userMapper;

    /**
     * login
     * @param username username
     * @param pwd pwd
     * @return boolean
     */
    @Override
    public boolean login(String username, String pwd) {
        UserEntity thisUser = userMapper.getUserByUsername(username);
        if (thisUser == null) {
            return false;
        }
        else {
            int ret = userMapper.checkPwd(username, pwd);
            //if username not found OR pwd does not match record
            return ret != 0;
        }
    }

    /**
     * getUserByUsername
     * @param username username
     * @return user object
     */
    @Override
    public UserEntity getUserByUsername (String username) {
        return userMapper.getUserByUsername(username);
    }

    /**
     * getUserByUuid
     * @param uuid uuid
     * @return user object
     */
    @Override
    public UserEntity getUserByUuid (String uuid) {
        return userMapper.getUserByUuid(uuid);
    }

    /**
     * checkPwd
     * @param thisUsername username
     * @param oldPwd pwd
     * @return 1=true, 0=false
     */
    @Override
    public int checkPwd (String thisUsername, String oldPwd) {
        return userMapper.checkPwd(thisUsername, oldPwd);
    }

    /**
     * checkPayPwd
     * @param uuid uuid
     * @param oldPayPwd payPwd
     * @return 1=true, 0=false
     */
    @Override
    public int checkPayPwd(String uuid, String oldPayPwd) {
        log.info("try userMapper.checkPayPwd");
        return userMapper.checkPayPwd(uuid, oldPayPwd);
    }

    /**
     * checkSecurityQuestion
     * @param uuid uuid
     * @param oldAnswer oldAnswer
     * @return 1=true, 0=false
     */
    @Override
    public int checkSecurityQuestion(String uuid, String oldAnswer) {
        return userMapper.checkSecurityQuestion(uuid, oldAnswer);
    }

    /**
     * 注册方法, 用户输入用户名、密码、支付密码、安全问题, 查询数据库检验是否有该账户,如果有,
     * 返回原先页面，显示用户名已存在，重新注册；如果没有，注册成功，返回原先页面，进行登录
     * @param userEntity user object
     * @param questionEntity question object
     * @return boolean
     */
    @Override
    public boolean registerUser(UserEntity userEntity, QuestionEntity questionEntity){
        String newUsername = userEntity.getUsername();
        UserEntity checkUser = userMapper.getUserByUsername(newUsername);
        // 如果数据库中已有该用户名
        if (checkUser != null) {
            log.info("registration failed: duplicate username");
            return false;
        }
        // 如果数据库中没有该用户名，可新建用户
        else {
            UUID uuid = UUID.randomUUID();
            String uuidStr = uuid.toString();
            userEntity.setUuid(uuidStr);
            questionEntity.setUuid(uuidStr);
            userMapper.createUser(userEntity);
            userMapper.updateSecurityQuestion(questionEntity);
            userMapper.createAccount(uuidStr);
            log.info(newUsername +" registered successfully");
            return true;
        }
    }

    /**
     * 更新账户密码
     */
    @Override
    public boolean updatePwd(String uuid, String newPwd){
        UserEntity thisUser = userMapper.getUserByUuid(uuid);
        // 如果数据库中未查到该账号:
        if (thisUser == null) {
            log.info("no matching record with username");
            return false;
        }
        else {
            thisUser.setPwd(newPwd);
            userMapper.updatePwd(thisUser);
            log.info(thisUser + "change login password successfully");
            return true;
        }
    }

    /**
     * 更新支付密码
     */
    @Override
    public boolean updatePayPwd(String uuid, String newPayPwd){
        UserEntity thisUser = userMapper.getUserByUuid(uuid);
        // 如果数据库中未查到该账号:
        if (thisUser == null) {
            log.info("account does not exist");
            return false;
        }
        else {
            thisUser.setPayPwd(newPayPwd);
            userMapper.updatePayPwd(thisUser);
            log.info(thisUser + "changed payment password successfully");
            return true;
        }
    }

    /**
     * 更新安全问题
     * @param  username username
     * @param  questionEntity question object
     * @return boolean
     */
    @Override
    public boolean updateQuestion(String username, QuestionEntity questionEntity){
        UserEntity thisUser = userMapper.getUserByUsername(username);
        // 如果数据库中未查到该账号:
        if (thisUser == null) {
            log.info("no matching record with username"+username);
            return false;
        }
        else {
            userMapper.updateSecurityQuestion(questionEntity);
            log.info(username + " changed security question successfully");
            return true;
        }
    }

    /**
     * (忘记密码->)返回安全问题序号，以便下一步验证安全问题
     * @param username username
     * @return questionIndex
     */
    @Override
    public int getQuestionIndexByUsername(String username){
        UserEntity thisUser = userMapper.getUserByUsername(username);
        if (thisUser == null){
            log.info("no matching record with username "+username);
            return -1;
        }
        else {
            QuestionEntity thisQuestion = userMapper.getSecurityQuestionByUsername(username);
            log.info("questionIndex found");
            return thisQuestion.getQuestionIndex();
        }
    }

    /**
     * 验证安全问题通过->重置登录密码（无需验证旧密码）
     * @param username username
     * @param answer answer
     * @param newPwd pwd
     * @return boolean
     */
    @Override
    public boolean resetPwd(String username, String answer, String newPwd){
        log.info("try resetting pwd");
        UserEntity thisUser = userMapper.getUserByUsername(username);
        //username must have matching record, so no need to check
        String thisUuid = thisUser.getUuid();
        int securityQuestionCheck = userMapper.checkSecurityQuestion(thisUuid, answer);
        if (securityQuestionCheck == 0){
            log.info("failed to reset pwd: wrong answer to the security question");
            return false;
        }
        else{
            thisUser.setPwd(newPwd);
            userMapper.updatePwd(thisUser);
            log.info("resetting pwd success for username "+username);
            return true;
        }
    }

    /**
     * 查看所有用户的注册信息，按照Spring Boot的设定，以Json的形式输送给用户端。
     * @return list of user objects
     */
    @Override
    public List<UserEntity> listAllUsers() {
        List<UserEntity> allUsers = userMapper.listAllUsers();
        if (allUsers != null){
            return userMapper.listAllUsers();
        }
        else {
            return null;
        }
    }
}
