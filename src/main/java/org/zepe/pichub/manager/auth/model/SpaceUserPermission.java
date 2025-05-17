package org.zepe.pichub.manager.auth.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zzpus
 * @datetime 2025/5/17 11:42
 * @description
 */
@Data
public class SpaceUserPermission implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 权限键
     */
    private String key;
    /**
     * 权限名称
     */
    private String name;
    /**
     * 权限描述
     */
    private String description;

}
