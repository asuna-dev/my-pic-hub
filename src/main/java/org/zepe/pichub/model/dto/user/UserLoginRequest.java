package org.zepe.pichub.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zzpus
 * @datetime 2025/4/28 01:08
 * @description
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;
}

