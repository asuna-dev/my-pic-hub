package org.zepe.pichub.model.dto.spaceuser;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zzpus
 * @datetime 2025/5/17 00:26
 * @description
 */
@Data
public class SpaceUserQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * ID
     */
    private Long id;
    /**
     * 空间 ID
     */
    private Long spaceId;
    /**
     * 用户 ID
     */
    private Long userId;
    /**
     * 空间角色：viewer/editor/admin
     */
    private String spaceRole;
}
