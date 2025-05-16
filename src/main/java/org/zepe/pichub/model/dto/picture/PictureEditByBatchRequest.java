package org.zepe.pichub.model.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zzpus
 * @datetime 2025/5/15 23:27
 * @description
 */
@Data
public class PictureEditByBatchRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 图片 id 列表
     */
    private List<Long> pictureIdList;
    /**
     * 命名规则
     */
    private String nameRule;
    /**
     * 空间 id
     */
    private Long spaceId;
    /**
     * 分类
     */
    private String category;
    /**
     * 标签
     */
    private List<String> tags;
}
