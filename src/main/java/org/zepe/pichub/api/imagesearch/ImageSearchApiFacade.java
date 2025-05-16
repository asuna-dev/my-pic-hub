package org.zepe.pichub.api.imagesearch;

import lombok.extern.slf4j.Slf4j;
import org.zepe.pichub.api.imagesearch.model.ImageSearchResult;
import org.zepe.pichub.api.imagesearch.sub.GetImageFirstUrlApi;
import org.zepe.pichub.api.imagesearch.sub.GetImageListApi;
import org.zepe.pichub.api.imagesearch.sub.GetImagePageUrlApi;

import java.util.List;

/**
 * @author zzpus
 * @datetime 2025/5/15 23:18
 * @description
 */
@Slf4j
public class ImageSearchApiFacade {

    /**
     * 搜索图片
     *
     * @param imageUrl
     * @return
     */
    public static List<ImageSearchResult> searchImage(String imageUrl) {
        String imagePageUrl = GetImagePageUrlApi.getImagePageUrl(imageUrl);
        String imageFirstUrl = GetImageFirstUrlApi.getImageFirstUrl(imagePageUrl);
        return GetImageListApi.getImageList(imageFirstUrl);
    }

    public static void main(String[] args) {
        // 测试以图搜图功能
        String imageUrl = "https://www.codefather.cn/logo.png";
        List<ImageSearchResult> resultList = searchImage(imageUrl);
        log.info("结果列表:{}", resultList);
    }
}

