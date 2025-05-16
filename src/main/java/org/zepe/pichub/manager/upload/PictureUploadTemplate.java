package org.zepe.pichub.manager.upload;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.CIObject;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.qcloud.cos.model.ciModel.persistence.ProcessResults;
import lombok.extern.slf4j.Slf4j;
import org.zepe.pichub.config.CosClientConfig;
import org.zepe.pichub.exception.BusinessException;
import org.zepe.pichub.exception.ErrorCode;
import org.zepe.pichub.manager.CosManager;
import org.zepe.pichub.model.dto.file.UploadPictureResult;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author zzpus
 * @datetime 2025/4/29 13:25
 * @description
 */
@Slf4j
public abstract class PictureUploadTemplate {
    protected static final Set<String> ALLOW_FORMAT_LIST = CollUtil.newHashSet("jpeg", "jpg", "png", "webp");
    protected static final long FILE_MAX_SIZE = 2 * 1024 * 1024L;
    @Resource
    protected CosManager cosManager;
    @Resource
    protected CosClientConfig cosClientConfig;

    /**
     * 模板方法，定义上传流程
     */
    public final UploadPictureResult uploadPicture(Object inputSource, String uploadPathPrefix) {
        UploadPictureResult result = null;
        // 1. 校验图片
        validPicture(inputSource);

        // 2. 图片上传地址
        String uuid = RandomUtil.randomString(16);
        String originFilename = getOriginFilename(inputSource);
        String uploadFilename =
            String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid, FileUtil.getSuffix(originFilename));
        String uploadPath = String.format("/%s/%s", uploadPathPrefix, uploadFilename);

        File file = null;
        try {
            file = File.createTempFile(uploadPath, null);
            // 处理文件来源
            processFile(inputSource, file);
            // 4. 上传图片到对象存储
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            ProcessResults processResults = putObjectResult.getCiUploadResult().getProcessResults();
            List<CIObject> objectList = processResults.getObjectList();
            if (CollUtil.isNotEmpty(objectList)) {
                CIObject compressObj = objectList.get(0);
                CIObject thumbnailObj = compressObj;
                if (objectList.size() > 1) {
                    thumbnailObj = objectList.get(1);
                }
                // 封装压缩图结果
                result = buildResult(originFilename, compressObj, thumbnailObj);
            } else {
                // 5. 封装原图返回结果
                result = buildResult(file, uploadPath, originFilename, imageInfo);
            }
            result.setPicColor(imageInfo.getAve());
            return result;
        } catch (Exception e) {
            log.error("图片上传到对象存储失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            deleteTempFile(file);
        }
    }

    /**
     * 校验输入源（本地文件或 URL）
     */
    protected abstract void validPicture(Object inputSource);

    /**
     * 获取输入源的原始文件名
     */
    protected abstract String getOriginFilename(Object inputSource);

    /**
     * 处理输入源并保存文件至file
     */
    protected abstract void processFile(Object inputSource, File file) throws Exception;

    /**
     * 封装返回结果
     */
    private UploadPictureResult buildResult(File file, String uploadPath, String originFilename, ImageInfo imageInfo) {
        int width = imageInfo.getWidth();
        int height = imageInfo.getHeight();

        return UploadPictureResult.builder()
            .picName(FileUtil.mainName(originFilename))
            .picWidth(width)
            .picHeight(height)
            .picScale(NumberUtil.round(width * 1.0 / height, 2).doubleValue())
            .picFormat(imageInfo.getFormat())
            .picSize(FileUtil.size(file))
            .url(cosClientConfig.getHost() + uploadPath)
            .build();
    }

    private UploadPictureResult buildResult(String originFilename, CIObject compress, CIObject thumbnail) {
        int width = compress.getWidth();
        int height = compress.getHeight();

        return UploadPictureResult.builder()
            .picName(FileUtil.mainName(originFilename))
            .picWidth(width)
            .picHeight(height)
            .picScale(NumberUtil.round(width * 1.0 / height, 2).doubleValue())
            .picFormat(compress.getFormat())
            .picSize(compress.getSize().longValue())
            .url(cosClientConfig.getHost() + "/" + compress.getKey())
            .thumbnailUrl(cosClientConfig.getHost() + "/" + thumbnail.getKey())
            .build();
    }

    /**
     * 删除临时文件
     */
    public void deleteTempFile(File file) {
        if (file == null) {
            return;
        }
        // 删除临时文件
        boolean deleteResult = file.delete();
        if (!deleteResult) {
            log.error("file delete error, filepath = {}", file.getAbsolutePath());
        }
    }

}

