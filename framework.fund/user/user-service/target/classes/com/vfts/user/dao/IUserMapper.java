package com.vfts.user.dao;

import com.vfts.user.entity.UserEntity;
import com.vfts.user.entity.QuestionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * IUserMapper interface
 * @author Axl
 */
@Mapper
public interface IUserMapper {

	/**
	 * createUser
	 * @param userEntity user object
	 */
	void createUser(UserEntity userEntity);

	/**
	 * createAccount
	 * @param uuid uuid
	 * @return 1=true, 0=false
	 */
	int createAccount(@Param("uuid") String uuid);

	/**
	 * updatePwd
	 * @param userEntity user object
	 * @return 1=true, 0=false
	 */
	int updatePwd(UserEntity userEntity);

	/**
	 * updatePayPwd
	 * @param userEntity user object
	 * @return 1=true, 0=false
	 */
	int updatePayPwd(UserEntity userEntity);

	/**
	 * getUserByUuid
	 * @param uuid uuid
	 * @return user object
	 */
	UserEntity getUserByUuid(@Param("uuid") String uuid);

	/**
	 * getUserByUsername
	 * @param username username
	 * @return user object
	 */
	UserEntity getUserByUsername(@Param("username") String username);

	/**
	 * getSecurityQuestionByUsername
	 * @param username username
	 * @return a security question object = {questionIndex:int, answer:String}
	 */
	QuestionEntity getSecurityQuestionByUsername(@Param("username") String username);

	/**
	 * listAllUsers
	 * @return user object
	 */
	List<UserEntity> listAllUsers();

	/**
	 * updateSecurityQuestion
	 * @param questionPair question object
	 * @return 1=true, 0=false
	 */
	int updateSecurityQuestion(QuestionEntity questionPair);

	/**
	 * checkPayPwd
	 * @param uuid uuid
	 * @param payPwd payPwd
	 * @return 1=true, 0=false
	 */
	int checkPayPwd(@Param("uuid") String uuid, @Param("payPwd") String payPwd);

	/**
	 * checkPwd
	 * @param username username
	 * @param pwd pwd
	 * @return 1=true, 0=false
	 */
	int checkPwd(@Param("username") String username, @Param("pwd") String pwd);

	/**
	 * checkSecurityQuestion
	 * @param uuid uuid
	 * @param answer answer
	 * @return 1=true, 0=false
	 */
	int checkSecurityQuestion(@Param("uuid") String uuid, @Param("answer") String answer);
}
