package org.zepe.pichub.model.dto.picture;

import lombok.Data;

/**
 * @author zzpus
 * @datetime 2025/4/29 16:45
 * @description
 */
@Data
public class PictureUploadByBatchRequest {
    /**
     * 搜索词
     */
    private String searchText;
    /**
     * 名称前缀
     */
    private String namePrefix;

    /**
     * 抓取数量
     */
    private Integer count = 10;
}
