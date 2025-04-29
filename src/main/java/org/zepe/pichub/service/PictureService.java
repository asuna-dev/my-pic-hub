package org.zepe.pichub.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;
import org.zepe.pichub.model.dto.picture.PictureQueryRequest;
import org.zepe.pichub.model.dto.picture.PictureReviewRequest;
import org.zepe.pichub.model.dto.picture.PictureUploadByBatchRequest;
import org.zepe.pichub.model.dto.picture.PictureUploadRequest;
import org.zepe.pichub.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import org.zepe.pichub.model.entity.User;
import org.zepe.pichub.model.vo.PictureVO;

/**
 * @author zzpus
 * @description 针对表【picture(图片)】的数据库操作Service
 * @createDate 2025-04-28 15:32:51
 */
public interface PictureService extends IService<Picture> {
    /**
     * 上传图片
     *
     * @param inputResource        目前支持url, MultipartFile
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */

    PictureVO uploadPicture(Object inputResource, PictureUploadRequest pictureUploadRequest, User loginUser);

    LambdaQueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    PictureVO getPictureVO(Picture picture);

    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage);

    /**
     * 图片审核
     *
     * @param pictureReviewRequest
     * @param loginUser
     */
    void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);

    void fillReviewParams(Picture picture, User loginUser);

    void validPicture(Picture picture);

    /**
     * 批量抓取和创建图片
     *
     * @param pictureUploadByBatchRequest
     * @param loginUser
     * @return 成功创建的图片数
     */
    int uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser);

    void clearPictureFile(Picture oldPicture);
}
