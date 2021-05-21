package com.vfts.user.iface;

import com.vfts.user.entity.UserEntity;
import com.vfts.user.entity.QuestionEntity;

import java.util.List;

/**
 * IUserService interface
 * @author Axl
 */
public interface IUserService {

    /**
     * login
     * @param username username
     * @param pwd pwd
     * @return boolean
     */
    boolean login(String username, String pwd);

    /**
     * getUserByUsername
     * @param username username
     * @return user object
     */
    UserEntity getUserByUsername (String username);

    /**
     * getUserByUuid
     * @param uuid uuid
     * @return user object
     */
    UserEntity getUserByUuid (String uuid);

    /**
     * checkPwd
     * @param thisUsername username
     * @param oldPwd pwd
     * @return 1=true, 0=false
     */
    int checkPwd (String thisUsername, String oldPwd);

    /**
     * checkPayPwd
     * @param uuid uuid
     * @param oldPayPwd payPwd
     * @return 1=true, 0=false
     */
    int checkPayPwd(String uuid, String oldPayPwd);

    /**
     * checkSecurityQuestion
     * @param uuid uuid
     * @param oldAnswer oldAnswer
     * @return 1=true, 0=false
     */
    int checkSecurityQuestion(String uuid, String oldAnswer);

    /**
     * registerUser
     * @param userEntity user object
     * @param questionEntity question object
     * @return boolean
     */
    boolean registerUser(UserEntity userEntity, QuestionEntity questionEntity);

    /**
     * updatePwd
     * @param uuid uuid
     * @param newPwd pwd
     * @return boolean
     */
    boolean updatePwd(String uuid, String newPwd);

    /**
     * resetPwd
     * @param username username
     * @param answer answer
     * @param newPwd newPwd
     * @return boolean
     */
    boolean resetPwd(String username, String answer, String newPwd);

    /**
     * updatePayPwd
     * @param uuid uuid
     * @param newPayPwd payPwd
     * @return boolean
     */
    boolean updatePayPwd(String uuid, String newPayPwd);

    /**
     * updateQuestion
     * @param uuid uuid
     * @param questionEntity question object
     * @return boolean
     */
    boolean updateQuestion(String uuid, QuestionEntity questionEntity);

    /**
     * getQuestionIndexByUsername
     * @param username username
     * @return index in range [1,5]
     */
    int getQuestionIndexByUsername(String username);

    /**
     * listAllUsers
     * @return list of user objects
     */
    List<UserEntity> listAllUsers();
}
