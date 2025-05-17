package org.zepe.pichub.model.dto.spaceuser;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zzpus
 * @datetime 2025/5/17 00:26
 * @description
 */
@Data
public class SpaceUserEditRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;
    /**
     * 空间角色：viewer/editor/admin
     */
    private String spaceRole;
}
