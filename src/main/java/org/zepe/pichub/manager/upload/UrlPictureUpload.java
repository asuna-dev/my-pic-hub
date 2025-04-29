package org.zepe.pichub.manager.upload;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zepe.pichub.exception.BusinessException;
import org.zepe.pichub.exception.ErrorCode;
import org.zepe.pichub.exception.ThrowUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author zzpus
 * @datetime 2025/4/29 13:38
 * @description
 */
@Slf4j
@Component
public class UrlPictureUpload extends PictureUploadTemplate {

    @Override
    protected void validPicture(Object inputSource) {
        String fileUrl = (String)inputSource;

        ThrowUtils.throwIf(StrUtil.isBlank(fileUrl), ErrorCode.PARAMS_ERROR, "文件地址不能为空");

        try {
            // 1. 验证 URL 格式
            new URL(fileUrl); // 验证是否是合法的 URL
        } catch (MalformedURLException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件地址格式不正确");
        }

        // 2. 校验 URL 协议
        ThrowUtils.throwIf(!(fileUrl.startsWith("http://") || fileUrl.startsWith("https://")),
            ErrorCode.PARAMS_ERROR,
            "仅支持 HTTP 或 HTTPS 协议的文件地址"
        );

        // 3. 发送 HEAD 请求以验证文件是否存在
        try (HttpResponse response = HttpUtil.createRequest(Method.HEAD, fileUrl).timeout(1500).execute()) {
            // 未正常返回，无需执行其他判断，可能不支持head访问
            // ThrowUtils.throwIf(response.getStatus() != HttpStatus.HTTP_OK, ErrorCode.PARAMS_ERROR, "文件url有误");
            if (response.getStatus() != HttpStatus.HTTP_OK) {
                log.warn("Head ERROR, url:{},code:{},message:{}", fileUrl, response.getStatus(), response.body());
                return;
            }

            // 4. 校验文件类型
            String contentType = response.header("Content-Type");
            contentType = FileUtil.getName(contentType);
            if (StrUtil.isNotBlank(contentType)) {
                // 允许的图片类型
                ThrowUtils.throwIf(!ALLOW_FORMAT_LIST.contains(contentType.toLowerCase()),
                    ErrorCode.PARAMS_ERROR,
                    "不支持的文件类型" + contentType
                );
            }
            // 5. 校验文件大小
            String contentLengthStr = response.header("Content-Length");
            if (StrUtil.isNotBlank(contentLengthStr)) {
                try {
                    long contentLength = Long.parseLong(contentLengthStr);
                    ThrowUtils.throwIf(contentLength > FILE_MAX_SIZE, ErrorCode.PARAMS_ERROR, "文件大小不能超过 2M");
                } catch (NumberFormatException e) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小格式错误");
                }
            }
        } catch (Exception e) {
            log.info("Head {} error", fileUrl, e);
        }
    }

    @Override
    protected String getOriginFilename(Object inputSource) {
        return FileUtil.getName((String)inputSource);
    }

    @Override
    protected void processFile(Object inputSource, File file) throws Exception {
        String fileUrl = (String)inputSource;
        HttpUtil.downloadFile(fileUrl, file, new StreamProgress() {
                @Override
                public void start() {
                    log.info("start download {}", fileUrl);
                }

                @Override
                public void progress(long total, long progressSize) {
                    if ((total != Long.MAX_VALUE && total > FILE_MAX_SIZE) || progressSize > FILE_MAX_SIZE) {
                        throw new BusinessException(ErrorCode.OPERATION_ERROR, "文件大小超出限制");
                    }
                }

                @Override
                public void finish() {
                    log.info("finish download {}", fileUrl);
                }
            }
        );
    }
}

