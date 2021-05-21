package com.vfts.user.controller;

import com.vfts.user.entity.UserEntity;
import com.vfts.user.entity.QuestionEntity;
import com.vfts.user.iface.IUserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;

/**
 * UserController class
 * specifies and implements APIs to be used by the frontend
 * @author Axl
 */
@Slf4j
@RestController
@RequestMapping(value = "/uiapi/user/*")
@CrossOrigin
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * 登陆方法, 用户输入邮箱和密码, 查询数据库检验是否有该账户,如果有,
     * 返回原先页面 ,登陆成功。
     * @param username username
     * @param pwd pwd
     * @return ResponseEntity<HashMap<String, String>> <uuid: thisUuid, username: thisUsername>
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ApiOperation(value = "登陆", httpMethod = "POST")
    public ResponseEntity<HashMap<String, String>> login(@RequestParam String username, @RequestParam String pwd) {
        try{
            System.out.println("try logging in");
            log.info("try logging in");
            if (userService.login(username, pwd)){
                //login successful
                log.info("login success");
                UserEntity thisUser = userService.getUserByUsername(username);
                String thisUuid = thisUser.getUuid();
                HashMap<String, String> response = new HashMap<>(2);
                response.put("uuid", thisUuid);
                response.put("username", username);
                System.out.println("login success");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("login BAD-REQUEST");
        log.info("Login BAD-REQUEST");
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    /**
     * 注册方法, 用户输入用户名、密码、支付密码、安全问题, 查询数据库检验是否有该账户,如果有,
     * 返回原先页面，显示用户名已存在，重新注册；如果没有，注册成功，返回原先页面，进行登录
     * @param username 用户名
     * @param pwd 用户密码
     * @param userPayPwd payPwd
     * @param questionIndex index
     * @param answer answer to the security question
     * @return uuid
     */
    @RequestMapping(value = "/register",method = RequestMethod.POST)
    @ApiOperation(value = "注册", httpMethod = "POST")
    public ResponseEntity<String> registerUser(@RequestParam String username, @RequestParam String pwd, @RequestParam String userPayPwd,
                             @RequestParam int questionIndex, @RequestParam String answer){
        UserEntity newUser = new UserEntity();
        newUser.setUsername(username);
        newUser.setPwd(pwd);
        newUser.setPayPwd(userPayPwd);
        //the null uuid in newQuestion will be updated in the registerUser() function
        QuestionEntity newQuestion = new QuestionEntity(null, questionIndex, answer);
        try{
            log.info("try registering");
            if (userService.registerUser(newUser, newQuestion)){
                log.info("registration success");
                return new ResponseEntity<>("Registration success", HttpStatus.OK);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("Registration BAD-REQUEST");
        return new ResponseEntity<>("Registration failed", HttpStatus.NO_CONTENT);
    }

    /**
     * 更新账户密码
     */
    @RequestMapping(value = "/updatePwd", method = RequestMethod.POST)
    @ApiOperation(value = "更新账户密码", httpMethod = "POST")
    public ResponseEntity<String> updatePwd(@RequestParam String uuid, @RequestParam String oldPwd, @RequestParam String newPwd) throws Exception {
        try{
            log.info("try updating login password");
            //check if oldPwd matches record
            UserEntity thisUser = userService.getUserByUuid(uuid);
            log.info("try: userService.getUserByUuid");
            String thisUsername = thisUser.getUsername();
            int ret = userService.checkPwd(thisUsername, oldPwd);
            log.info("try: userService.checkPwd");
            if (ret == 0){
                log.info("oldPwd does not match record");
                return new ResponseEntity<>("Wrong old password, please try again", HttpStatus.NO_CONTENT);
            }
            else{
                if (userService.updatePwd(uuid, newPwd)){
                    log.info("updating pwd success");
                    return new ResponseEntity<>("Updating on login password success", HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            throw new Exception ("failed to update pwd");
        }
        log.info("Updating pwd BAD-REQUEST");
        return new ResponseEntity<>("Updating pwd failed", HttpStatus.NO_CONTENT);
    }

    /**
     * 更新支付密码
     */
    @RequestMapping(value = "/updatePayPwd", method = RequestMethod.POST)
    @ApiOperation(value = "更新支付密码", httpMethod = "POST")
    public ResponseEntity<String> updatePayPwd(@RequestParam String uuid, @RequestParam String oldPayPwd, @RequestParam String newPayPwd) throws Exception {
        try{
            log.info("try updating payment password");

            //check if oldPayPwd matches record
            int ret = userService.checkPayPwd(uuid, oldPayPwd);

            //oldPayPwd does not match
            if (ret == 0) {
                log.info("oldPayPwd does not match record");
                return new ResponseEntity<>("Wrong old payment password, please try again", HttpStatus.NO_CONTENT);
            }
            else{
                if (userService.updatePayPwd(uuid, newPayPwd)){
                    log.info("updating payPwd success");
                    return new ResponseEntity<>("Updating on payment password success", HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            throw new Exception ("failed to update payPwd");
        }
        log.info("Updating payPwd BAD-REQUEST");
        return new ResponseEntity<>("Updating payPwd failed", HttpStatus.NO_CONTENT);
    }

    /**
     * 获取安全问题序号（忘记登录密码->验证安全问题）
     * @param username username
     * @return ResponseEntity<Integer> questionIndex
     */
    @RequestMapping(value = "/getSecurityQuestionIndex", method = RequestMethod.POST)
    @ApiOperation(value = "获取安全问题序号", httpMethod = "POST")
    public ResponseEntity<Integer> getQuestionIndexByUsername(@RequestParam String username) throws Exception {
        try {
            log.info("try getting security question index by username " + username);
            int questionIndex = userService.getQuestionIndexByUsername(username);
            if (questionIndex != -1){
                log.info("getting security question index success");
                return new ResponseEntity<>(questionIndex, HttpStatus.OK);
            }
        } catch (Exception e) {
            throw new Exception ("failed to get security question for username "+username);
        }
        log.info("Getting security question index BAD-REQUEST");
        return new ResponseEntity<>(-1, HttpStatus.NO_CONTENT);
    }

    /**
     * 验证安全问题通过->重置登录密码（无需验证旧密码）
     * @param username username
     * @param answer answer to the security question
     * @param newPwd newPwd
     * @return ResponseEntity<String>
     */
    @RequestMapping(value = "/resetPwd", method = RequestMethod.POST)
    @ApiOperation(value = "重置登录密码", httpMethod = "POST")
    public ResponseEntity<String> resetPwd(@RequestParam String username, @RequestParam String answer, @RequestParam String newPwd) throws Exception {
        log.info("try resetting pwd");
        try {
            if (userService.resetPwd(username, answer, newPwd)){
                log.info("resetting pwd success");
                return new ResponseEntity<>("Resetting pwd success", HttpStatus.OK);
            }
        } catch (Exception e){
            throw new Exception("failed to reset pwd: wrong username or wrong answer to security question");
        }
        log.info("failed to reset pwd");
        return new ResponseEntity<>("Failed to reset pwd", HttpStatus.NO_CONTENT);
    }

    /**
     * 更新安全问题
     */
    @RequestMapping(value = "/updateQuestion", method = RequestMethod.POST)
    @ApiOperation(value = "更新安全问题", httpMethod = "POST")
    public ResponseEntity<String> updateQuestion(@RequestParam String uuid, @RequestParam String oldAnswer, @RequestParam int newQuestionIndex, @RequestParam String newAnswer){
        UserEntity thisUser = userService.getUserByUuid(uuid);
        if (thisUser == null){
            //no matching user with uuid
            return new ResponseEntity<>("Updating security question failed: no matching user with uuid"+uuid, HttpStatus.NO_CONTENT);
        }
        else{//matching user found
            //check if oldAnswer matches record
            int oldAnswerCheck = userService.checkSecurityQuestion(uuid, oldAnswer);
            if (oldAnswerCheck == 0){
                //does not match record
                log.info("failed to update security question: wrong answer to old security question");
                return new ResponseEntity<>("Updating security question failed: wrong answer to old security question", HttpStatus.NO_CONTENT);
            }
            else{
                String thisUsername = thisUser.getUsername();
                QuestionEntity newQuestion = new QuestionEntity(uuid, newQuestionIndex,newAnswer);
                try{
                    log.info("try updating security question");
                    if (userService.updateQuestion(thisUsername, newQuestion)){
                        log.info("updating security question success");
                        return new ResponseEntity<>("Updating on safety question success", HttpStatus.OK);
                    }
                } catch(Exception e) {
                    throw new RuntimeException(e);
                }
                log.info("Updating security question BAD-REQUEST");
                return new ResponseEntity<>("Updating security question failed", HttpStatus.NO_CONTENT);
            }
        }
    }

    /**
    * 查看所有用户的注册信息，按照Spring Boot的设定，以Json的形式输送给用户端。
    * @return list of user objects
    */
    @RequestMapping(value="/all", method = RequestMethod.POST)
    @ApiOperation(value = "全部", httpMethod = "POST")
    public ResponseEntity<List<UserEntity>> listAllUsers() {
        try {
            log.info("try returning all users");
            List<UserEntity> listOfAll = userService.listAllUsers();
            if (listOfAll != null){
                log.info("return all users");
                return new ResponseEntity<>(listOfAll, HttpStatus.OK);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("Listing all users BAD-REQUEST");
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }
}
