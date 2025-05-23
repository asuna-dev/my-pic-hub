package org.zepe.pichub.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zzpus
 * @datetime 2025/4/28 00:37
 * @description
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 账号
     */
    private String userAccount;
    /**
     * 昵称
     */
    private String userName;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    private String checkPassword;
}

