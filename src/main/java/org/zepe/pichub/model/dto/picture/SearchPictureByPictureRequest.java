package org.zepe.pichub.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zzpus
 * @datetime 2025/5/15 23:27
 * @description
 */
@Data
public class SearchPictureByPictureRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 图片 id
     */
    private Long pictureId;
}
