package org.zepe.pichub.api.imagesearch.model;

import lombok.Data;

/**
 * @author zzpus
 * @datetime 2025/5/15 23:04
 * @description
 */
@Data
public class ImageSearchResult {

    /**
     * 缩略图地址
     */
    private String thumbUrl;

    /**
     * 来源地址
     */
    private String fromUrl;
}

