package org.zepe.pichub.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zzpus
 * @datetime 2025/5/15 23:27
 * @description
 */
@Data
public class SearchPictureByColorRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 图片主色调
     */
    private String picColor;
    /**
     * 空间 id
     */
    private Long spaceId;
}
