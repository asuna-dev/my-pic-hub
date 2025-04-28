package org.zepe.pichub.model.dto.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.zepe.pichub.common.PageRequest;

import java.io.Serializable;

/**
 * @author zzpus
 * @datetime 2025/4/28 12:23
 * @description
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserQueryRequest extends PageRequest implements Serializable {

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
     * 账号
     */
    private String userAccount;
    /**
     * 简介
     */
    private String userProfile;
    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;
}

