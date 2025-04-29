package org.zepe.pichub.manager;

import cn.hutool.core.io.FileUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.*;

import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import com.qcloud.cos.transfer.Upload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.qcloud.cos.transfer.TransferManager;
import org.springframework.web.multipart.MultipartFile;
import org.zepe.pichub.config.CosClientConfig;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zzpus
 * @datetime 2025/4/28 14:59
 * @description
 */
@Slf4j
@Service
public class CosManager {
    @Resource
    CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;

    @PreDestroy
    public void close() {
        // log.info("COSClient shutdown");
        cosClient.shutdown();
    }

    /**
     * 上传对象（附带图片信息）
     *
     * @param key  唯一键
     * @param file 文件
     */
    public PutObjectResult putPictureObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        // 对图片进行处理（获取基本信息也被视作为一种处理）
        PicOperations picOperations = new PicOperations();
        // 1 表示返回原图信息
        picOperations.setIsPicInfo(1);
        List<PicOperations.Rule> rules = new ArrayList<>();
        // 图片压缩（转成 webp 格式）
        String webpKey = FileUtil.mainName(key) + ".webp";
        PicOperations.Rule compressRule = new PicOperations.Rule();
        compressRule.setRule("imageMogr2/format/webp");
        compressRule.setBucket(cosClientConfig.getBucket());
        compressRule.setFileId(webpKey);
        rules.add(compressRule);
        // 缩略图处理(>50kB)
        if (file.length() > 50 * 1024) {
            PicOperations.Rule thumbnailRule = new PicOperations.Rule();
            thumbnailRule.setBucket(cosClientConfig.getBucket());
            String thumbnailKey = FileUtil.mainName(key) + "_thumbnail." + FileUtil.getSuffix(key);
            thumbnailRule.setFileId(thumbnailKey);
            // 缩放规则 /thumbnail/<Width>x<Height>>（如果大于原图宽高，则不处理）
            thumbnailRule.setRule(String.format("imageMogr2/thumbnail/%sx%s>", 128, 128));
            rules.add(thumbnailRule);
        }
        // 构造处理参数
        picOperations.setRules(rules);
        putObjectRequest.setPicOperations(picOperations);
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        // 删除原图
        deleteObject(key);
        return putObjectResult;
    }

    public PutObjectResult putPictureObject(String key, InputStream inputStream) {
        PutObjectRequest putObjectRequest =
            new PutObjectRequest(cosClientConfig.getBucket(), key, inputStream, new ObjectMetadata());
        // 对图片进行处理（获取基本信息也被视作为一种处理）
        PicOperations picOperations = new PicOperations();
        // 1 表示返回原图信息
        picOperations.setIsPicInfo(1);
        // 构造处理参数
        putObjectRequest.setPicOperations(picOperations);

        return cosClient.putObject(putObjectRequest);
    }

    @Async("AsyncExecutor")
    public void deleteObject(String key) {
        log.info("ClearFile-3:{}", key);
        try {
            cosClient.deleteObject(cosClientConfig.getBucket(), key);
        } catch (Exception e) {
            log.error("ClearFile-4:{}", key, e);
        }

    }

    public PutObjectResult putPictureObject(String key, MultipartFile multipartFile) throws IOException {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            // 元信息配置
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(multipartFile.getSize());
            metadata.setContentType(multipartFile.getContentType());

            PutObjectRequest putObjectRequest =
                new PutObjectRequest(cosClientConfig.getBucket(), key, inputStream, metadata);
            // 对图片进行处理（获取基本信息也被视作为一种处理）
            PicOperations picOperations = new PicOperations();
            // 1 表示返回原图信息
            picOperations.setIsPicInfo(1);
            // 构造处理参数
            putObjectRequest.setPicOperations(picOperations);

            return cosClient.putObject(putObjectRequest);
        }

    }

    // 上传文件(服务器不存储临时文件)
    public PutObjectResult uploadToCOS(String key, MultipartFile multipartFile) throws Exception {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            // 元信息配置
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(multipartFile.getSize());
            metadata.setContentType(multipartFile.getContentType());

            // 创建上传请求
            PutObjectRequest putObjectRequest =
                new PutObjectRequest(cosClientConfig.getBucket(), key, inputStream, metadata);

            // 上传文件
            return cosClient.putObject(putObjectRequest);
        }
    }

    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 下载对象
     *
     * @param key 唯一键
     */
    public COSObject getObject(String key) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
        return cosClient.getObject(getObjectRequest);
    }

}
