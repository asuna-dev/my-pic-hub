package org.zepe.pichub.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.multipart.MultipartFile;
import org.zepe.pichub.model.dto.picture.PictureQueryRequest;
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
     * @param multipartFile
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, User loginUser);

    LambdaQueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    PictureVO getPictureVO(Picture picture);

    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage);

    void validPicture(Picture picture);
}
