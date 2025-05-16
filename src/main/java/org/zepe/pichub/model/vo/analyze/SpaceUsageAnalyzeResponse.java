package org.zepe.pichub.model.vo.analyze;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zzpus
 * @datetime 2025/5/16 21:25
 * @description
 */
@Data
public class SpaceUsageAnalyzeResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 已使用大小
     */
    private Long usedSize;
    /**
     * 总大小
     */
    private Long maxSize;
    /**
     * 空间使用比例
     */
    private Double sizeUsageRatio;
    /**
     * 当前图片数量
     */
    private Long usedCount;
    /**
     * 最大图片数量
     */
    private Long maxCount;
    /**
     * 图片数量占比
     */
    private Double countUsageRatio;
}
