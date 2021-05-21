package com.vfts.user.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UserEntity class
 * @author Axl
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    private String uuid = null;
    private String username;
    private String pwd;
    private String payPwd;


    /**Overload constructor UserEntity for registerUser()
     * @param username username
     * @param pwd pwd
     * @param payPwd payPwd
     */
    public UserEntity(String username, String pwd, String payPwd) {
        this.username = username;
        this.pwd = pwd;
        this.payPwd = payPwd;
    }

}
