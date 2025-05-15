package org.zepe.pichub.model.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zzpus
 * @datetime 2025/5/14 14:42
 * @description
 */
@Data
public class SpaceEditRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 空间 id
     */
    private Long id;
    /**
     * 空间名称
     */
    private String spaceName;
}
