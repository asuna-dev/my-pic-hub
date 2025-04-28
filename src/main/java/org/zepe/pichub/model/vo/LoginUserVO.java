package org.zepe.pichub.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zzpus
 * @datetime 2025/4/28 01:09
 * @description
 */
@Data
public class LoginUserVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 用户 id
     */
    private Long id;
    /**
     * 账号
     */
    private String userAccount;
    /**
     * 用户昵称
     */
    private String userName;
    /**
     * 用户头像
     */
    private String userAvatar;
    /**
     * 用户简介
     */
    private String userProfile;
    /**
     * 用户角色：user/admin
     */
    private String userRole;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
}
