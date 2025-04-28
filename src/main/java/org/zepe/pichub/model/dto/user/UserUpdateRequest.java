package org.zepe.pichub.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zzpus
 * @datetime 2025/4/28 12:23
 * @description
 */
@Data
public class UserUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;
    /**
     * 用户昵称
     */
    private String userName;
    /**
     * 用户头像
     */
    private String userAvatar;
    /**
     * 简介
     */
    private String userProfile;
    /**
     * 用户角色：user/admin
     */
    private String userRole;
}

