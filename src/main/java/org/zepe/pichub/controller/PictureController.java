package org.zepe.pichub.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zepe.pichub.annotation.AuthCheck;
import org.zepe.pichub.common.DeleteRequest;
import org.zepe.pichub.common.Response;
import org.zepe.pichub.constant.UserConstant;
import org.zepe.pichub.exception.BusinessException;
import org.zepe.pichub.exception.ErrorCode;
import org.zepe.pichub.exception.ThrowUtils;
import org.zepe.pichub.model.dto.picture.*;
import org.zepe.pichub.model.entity.Picture;
import org.zepe.pichub.model.entity.Space;
import org.zepe.pichub.model.entity.User;
import org.zepe.pichub.model.enums.PictureReviewStatusEnum;
import org.zepe.pichub.model.vo.PictureTagCategory;
import org.zepe.pichub.model.vo.PictureVO;
import org.zepe.pichub.service.PictureService;
import org.zepe.pichub.service.SpaceService;
import org.zepe.pichub.service.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author zzpus
 * @datetime 2025/4/28 16:18
 * @description
 */
@Slf4j
@RestController
@RequestMapping("/picture")
public class PictureController {
    @Resource
    private PictureService pictureService;
    @Resource
    private UserService userService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private SpaceService spaceService;

    // 缓存 5 分钟移除
    private final Cache<String, String> LOCAL_CACHE = Caffeine.newBuilder()
        .initialCapacity(1024)
        .maximumSize(10000L)
        .expireAfterWrite(140L, TimeUnit.SECONDS)
        .build();

    /**
     * 上传图片（可重新上传）
     */
    @PostMapping("/upload")
    public Response<PictureVO> uploadPicture(@RequestPart("file") MultipartFile multipartFile,
                                             PictureUploadRequest pictureUploadRequest, HttpServletRequest request) {

        User loginUser = userService.getLoginUser(request);
        PictureVO pictureVO = pictureService.uploadPicture(multipartFile, pictureUploadRequest, loginUser);
        return Response.success(pictureVO);
    }

    /**
     * 通过 URL 上传图片（可重新上传）
     */
    @PostMapping("/upload/url")
    public Response<PictureVO> uploadPictureByUrl(@RequestBody PictureUploadRequest pictureUploadRequest,
                                                  HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        String fileUrl = pictureUploadRequest.getFileUrl();
        PictureVO pictureVO = pictureService.uploadPicture(fileUrl, pictureUploadRequest, loginUser);
        return Response.success(pictureVO);
    }

    /**
     * 删除图片
     */
    @PostMapping("/delete")
    public Response<Boolean> deletePicture(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        pictureService.deletePicture(deleteRequest.getId(), userService.getLoginUser(request));

        return Response.success(true);
    }

    /**
     * 更新图片（仅管理员可用）
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Response<Boolean> updatePicture(@RequestBody PictureUpdateRequest pictureUpdateRequest,
                                           HttpServletRequest request) {
        if (pictureUpdateRequest == null || pictureUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 将实体类和 DTO 进行转换  
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureUpdateRequest, picture);
        // 注意将 list 转为 string  
        picture.setTags(JSONUtil.toJsonStr(pictureUpdateRequest.getTags()));
        // 数据校验  
        pictureService.validPicture(picture);
        // 判断是否存在  
        long id = pictureUpdateRequest.getId();
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // fill审核参数
        pictureService.fillReviewParams(picture, userService.getLoginUser(request));
        // 操作数据库  
        boolean result = pictureService.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return Response.success(true);
    }

    @PostMapping("/review")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Response<Boolean> doPictureReview(@RequestBody PictureReviewRequest pictureReviewRequest,
                                             HttpServletRequest request) {
        ThrowUtils.throwIf(pictureReviewRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        pictureService.doPictureReview(pictureReviewRequest, loginUser);
        return Response.success(true);
    }

    /**
     * 根据 id 获取图片（仅管理员可用）
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Response<Picture> getPictureById(long id, HttpServletRequest request) {
        // 查询数据库
        Picture picture = pictureService.getById(id);
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return Response.success(picture);
    }

    /**
     * 根据 id 获取图片（封装类）
     */
    @GetMapping("/get/vo")
    public Response<PictureVO> getPictureVOById(long id, HttpServletRequest request) {
        // 查询数据库
        Picture picture = pictureService.getById(id);
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR);
        // 空间权限校验
        Long spaceId = picture.getSpaceId();
        if (spaceId != null) {
            User loginUser = userService.getLoginUser(request);
            pictureService.checkPictureAuth(loginUser, picture);
        }
        return Response.success(pictureService.getPictureVO(picture));
    }

    /**
     * 分页获取图片列表（仅管理员可用）
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Response<Page<Picture>> listPictureByPage(@RequestBody PictureQueryRequest pictureQueryRequest) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        // 查询数据库  
        Page<Picture> picturePage =
            pictureService.page(new Page<>(current, size), pictureService.getQueryWrapper(pictureQueryRequest));
        return Response.success(picturePage);
    }

    /**
     * 分页获取图片列表（封装类）
     */
    @Deprecated
    @PostMapping("/list/page/vo")
    public Response<Page<PictureVO>> listPictureVOByPage(@RequestBody PictureQueryRequest pictureQueryRequest,
                                                         HttpServletRequest request) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        // 空间权限校验
        Long spaceId = pictureQueryRequest.getSpaceId();
        // 公开图库
        if (spaceId == null) {
            // 普通用户默认只能查看已过审的公开数据
            pictureQueryRequest.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
            pictureQueryRequest.setNullSpaceId(true);
        } else {
            // 私有空间
            User loginUser = userService.getLoginUser(request);
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
            if (!loginUser.getId().equals(space.getUserId())) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有空间权限");
            }
        }

        // 构建缓存 key
        String queryCondition = JSONUtil.toJsonStr(pictureQueryRequest);
        String hashKey = DigestUtils.md5DigestAsHex(queryCondition.getBytes());
        String cachedKey = "yupicture:listPictureVOByPage:" + hashKey;
        Page<PictureVO> cachedPage = null;
        // 1 查本地缓存
        String cachedValue = LOCAL_CACHE.getIfPresent(cachedKey);

        // 本地缓存不为空
        if (cachedValue != null) {
            cachedPage = JSONUtil.toBean(cachedValue, Page.class);
            return Response.success(cachedPage);
        }

        // 2 查redis
        ValueOperations<String, String> valueOps = stringRedisTemplate.opsForValue();
        cachedValue = valueOps.get(cachedKey);
        if (cachedValue != null) {
            cachedPage = JSONUtil.toBean(cachedValue, Page.class);
            // 存入本地缓存
            LOCAL_CACHE.put(cachedKey, cachedValue);
            return Response.success(cachedPage);
        }

        // 3 查询数据库
        Page<Picture> picturePage =
            pictureService.page(new Page<>(current, size), pictureService.getQueryWrapper(pictureQueryRequest));
        // 获取封装类
        Page<PictureVO> pictureVOPage = pictureService.getPictureVOPage(picturePage);

        // 存入 Redis 和 本地缓存
        String cacheValue = JSONUtil.toJsonStr(pictureVOPage);

        LOCAL_CACHE.put(cachedKey, cacheValue);
        // 5 - 10 分钟随机过期，防止雪崩
        int cacheExpireTime = 120 + RandomUtil.randomInt(0, 120);
        valueOps.set(cachedKey, cacheValue, cacheExpireTime, TimeUnit.SECONDS);

        // 返回结果
        return Response.success(pictureVOPage);
    }

    /**
     * 编辑图片（给用户使用）
     */
    @PostMapping("/edit")
    public Response<Boolean> editPicture(@RequestBody PictureEditRequest pictureEditRequest,
                                         HttpServletRequest request) {
        if (pictureEditRequest == null || pictureEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 在此处将实体类和 DTO 进行转换
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureEditRequest, picture);
        // 注意将 list 转为 string
        picture.setTags(JSONUtil.toJsonStr(pictureEditRequest.getTags()));

        pictureService.editPicture(picture, userService.getLoginUser(request));

        return Response.success(true);
    }

    // todo 批量爬取图片时预设分类和标签
    @PostMapping("/upload/batch")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Response<Integer> uploadPictureByBatch(@RequestBody PictureUploadByBatchRequest pictureUploadByBatchRequest,
                                                  HttpServletRequest request) {
        ThrowUtils.throwIf(pictureUploadByBatchRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        int uploadCount = pictureService.uploadPictureByBatch(pictureUploadByBatchRequest, loginUser);
        return Response.success(uploadCount);
    }

    @GetMapping("/tag_category")
    public Response<PictureTagCategory> listPictureTagCategory() {
        PictureTagCategory pictureTagCategory = new PictureTagCategory();
        List<String> tagList =
            Arrays.asList("热门", "搞笑", "生活", "高清", "艺术", "校园", "背景", "简历", "创意", "动漫");
        List<String> categoryList = Arrays.asList("模板", "电商", "表情包", "素材", "海报", "二次元");
        pictureTagCategory.setTagList(tagList);
        pictureTagCategory.setCategoryList(categoryList);
        return Response.success(pictureTagCategory);
    }

}
