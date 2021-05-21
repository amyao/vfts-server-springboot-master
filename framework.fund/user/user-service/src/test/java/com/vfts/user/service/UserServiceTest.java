package com.vfts.user.service;

import com.vfts.user.entity.QuestionEntity;
import com.vfts.user.entity.UserEntity;
import com.vfts.user.iface.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.lang.String;


@Slf4j
@SpringBootTest(classes = {com.vfts.user.iface.IUserService.class})
@ExtendWith(SpringExtension.class)
@SpringBootConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:springMybatis.xml"})
public class UserServiceTest {

    @Autowired
    private IUserService userService;

    @Test
    public void registerUserTest(){
        UserEntity newUser = new UserEntity("amy", "67890", "678910");
        QuestionEntity newQuestion = new QuestionEntity("xxxxx",3, "agsdyfiu");
        boolean registerTest = userService.registerUser(newUser, newQuestion);
        log.info("[UserServiceTest] registerUserTest>>>>>>{}", registerTest);
        assertTrue(registerTest);
    }
    @Test
    public void loginTest(){
        String username = "amy";
        String pwd = "67890";
        boolean loginTest = userService.login(username, pwd);
        log.info("[UserServiceTest] loginTest>>>>>>{}", loginTest);
        assertTrue(loginTest);
    }

    @Test
    public void updatePwdTest(){
        String uuid = "a810a6a1-ced6-4f7e-8be3-2081edb5f1d1";
        String newPwd = "abcde";
        boolean updatePwdTest = userService.updatePwd(uuid, newPwd);
        log.info("[UserServiceTest] updatePwdTest>>>>>>{}", updatePwdTest);
        assertTrue(updatePwdTest);
    }

    @Test
    public void updatePayPwdTest(){
        String uuid = "a810a6a1-ced6-4f7e-8be3-2081edb5f1d1";
        String newPayPwd = "fghijk";
        boolean updatePayPwdTest = userService.updatePayPwd(uuid, newPayPwd);
        log.info("[UserServiceTest] updatePayPwdTest>>>>>>{}", updatePayPwdTest);
        assertTrue(updatePayPwdTest);
    }

    @Test
    public void updateQuestionTest(){
        String username = "Sunny";
        QuestionEntity newQuestion = new QuestionEntity("a810a6a1-ced6-4f7e-8be3-2081edb5f1d1",2, "IDKAGAIN");
        boolean updateQuestionTest = userService.updateQuestion(username, newQuestion);
        log.info("[UserServiceTest] updateQuestionTest>>>>>>{}", updateQuestionTest);
        assertTrue(updateQuestionTest);
    }

    @Test
    public void getQuestionIndexByUsernameTest(){
        String username = "Sunny";
        int getQuestionIndexByUsernameTest = userService.getQuestionIndexByUsername(username);
        log.info("[UserServiceTest] getQuestionIndexByUsernameTest>>>>>>{}", getQuestionIndexByUsernameTest);
        assertNotNull(getQuestionIndexByUsernameTest);
    }

    @Test
    public void resetPwdTest(){
        String username = "Sunny";
        String answer = "IDKAGAIN";
        String newPwd = "hhhhhh";
        boolean resetPwdTest = userService.resetPwd(username, answer, newPwd);
        log.info("[UserServiceTest] resetPwdTest>>>>>>{}", resetPwdTest);
        assertTrue(resetPwdTest);
    }

    @Test
    public void checkPayPwdTest(){
        String uuid, oldPayPwd;
        uuid = "6630f407-7d5e-4481-aafd-9fba2f0f2694";
        oldPayPwd = "54321";
        int result = userService.checkPayPwd(uuid, oldPayPwd);
        log.info("[UserServiceTest] checkPayPwdTest>>>>>>{}", result);
        assertTrue(result == 1);
    }
}