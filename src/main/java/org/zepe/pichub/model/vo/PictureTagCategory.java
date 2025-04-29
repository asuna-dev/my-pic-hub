package org.zepe.pichub.model.vo;

/**
 * @author zzpus
 * @datetime 2025/4/28 18:48
 * @description
 */

import lombok.Data;

import java.util.List;

/**
 * 图片标签分类列表视图
 */
@Data
public class PictureTagCategory {

    /**
     * 标签列表
     */
    private List<String> tagList;

    /**
     * 分类列表
     */
    private List<String> categoryList;
}
