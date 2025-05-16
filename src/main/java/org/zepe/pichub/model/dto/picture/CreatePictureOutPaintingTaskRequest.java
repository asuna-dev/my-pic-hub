package org.zepe.pichub.model.dto.picture;

import lombok.Data;
import org.zepe.pichub.api.aliyunai.model.CreateOutPaintingTaskRequest;

import java.io.Serializable;

/**
 * @author zzpus
 * @datetime 2025/5/16 17:36
 * @description
 */
@Data
public class CreatePictureOutPaintingTaskRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 图片 id
     */
    private Long pictureId;
    /**
     * 扩图参数
     */
    private CreateOutPaintingTaskRequest.Parameters parameters;
}
