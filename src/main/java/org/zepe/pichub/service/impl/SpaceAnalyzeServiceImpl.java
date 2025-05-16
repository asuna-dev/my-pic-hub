package org.zepe.pichub.service.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.zepe.pichub.exception.BusinessException;
import org.zepe.pichub.exception.ErrorCode;
import org.zepe.pichub.exception.ThrowUtils;
import org.zepe.pichub.mapper.SpaceMapper;
import org.zepe.pichub.model.dto.space.analyze.*;
import org.zepe.pichub.model.entity.Picture;
import org.zepe.pichub.model.entity.Space;
import org.zepe.pichub.model.entity.User;
import org.zepe.pichub.model.vo.analyze.*;
import org.zepe.pichub.service.PictureService;
import org.zepe.pichub.service.SpaceAnalyzeService;
import org.zepe.pichub.service.SpaceService;
import org.zepe.pichub.service.UserService;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author zzpus
 * @datetime 2025/5/16 21:27
 * @description
 */
@Service
public class SpaceAnalyzeServiceImpl implements SpaceAnalyzeService {
    @Resource
    private SpaceService spaceService;
    @Resource
    private SpaceMapper spaceMapper;
    @Resource
    private UserService userService;
    @Resource
    private PictureService pictureService;

    private static void fillAnalyzeQueryWrapper(SpaceAnalyzeRequest spaceAnalyzeRequest,
                                                QueryWrapper<Picture> queryWrapper) {
        if (spaceAnalyzeRequest.isQueryAll()) {
            return;
        }
        if (spaceAnalyzeRequest.isQueryPublic()) {
            queryWrapper.isNull("spaceId");
            return;
        }
        Long spaceId = spaceAnalyzeRequest.getSpaceId();
        if (spaceId != null) {
            queryWrapper.eq("spaceId", spaceId);
            return;
        }
        throw new BusinessException(ErrorCode.PARAMS_ERROR, "未指定查询范围");
    }

    /**
     * 获取空间使用分析数据
     *
     * @param spaceUsageAnalyzeRequest SpaceUsageAnalyzeRequest 请求参数
     * @param loginUser                当前登录用户
     * @return SpaceUsageAnalyzeResponse 分析结果
     */
    @Override
    public SpaceUsageAnalyzeResponse getSpaceUsageAnalyze(SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest,
                                                          User loginUser) {
        ThrowUtils.throwIf(spaceUsageAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);

        checkSpaceAnalyzeAuth(spaceUsageAnalyzeRequest, loginUser);

        // 封装返回结果
        SpaceUsageAnalyzeResponse response = new SpaceUsageAnalyzeResponse();

        if (spaceUsageAnalyzeRequest.isQueryAll() || spaceUsageAnalyzeRequest.isQueryPublic()) {
            QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
            fillAnalyzeQueryWrapper(spaceUsageAnalyzeRequest, queryWrapper);

            queryWrapper.select("picSize");
            List<Object> pictureObjList = pictureService.getBaseMapper().selectObjs(queryWrapper);
            long usedSize =
                pictureObjList.stream().mapToLong(result -> result instanceof Long ? (Long)result : 0).sum();
            long usedCount = pictureObjList.size();
            response.setUsedSize(usedSize);
            response.setUsedCount(usedCount);
            // 公共图库无上限、无比例
            response.setMaxSize(null);
            response.setSizeUsageRatio(null);
            response.setMaxCount(null);
            response.setCountUsageRatio(null);
        } else {
            Space space = spaceService.getById(spaceUsageAnalyzeRequest.getSpaceId());
            // 构造返回结果
            response.setUsedSize(space.getTotalSize());
            response.setMaxSize(space.getMaxSize());
            // 后端直接算好百分比，这样前端可以直接展示
            double sizeUsageRatio =
                NumberUtil.round(space.getTotalSize() * 100.0 / space.getMaxSize(), 2).doubleValue();
            response.setSizeUsageRatio(sizeUsageRatio);
            response.setUsedCount(space.getTotalCount());
            response.setMaxCount(space.getMaxCount());
            double countUsageRatio =
                NumberUtil.round(space.getTotalCount() * 100.0 / space.getMaxCount(), 2).doubleValue();
            response.setCountUsageRatio(countUsageRatio);
        }
        return response;

    }

    private void checkSpaceAnalyzeAuth(SpaceAnalyzeRequest spaceAnalyzeRequest, User loginUser) {
        // 检查权限
        if (spaceAnalyzeRequest.isQueryAll() || spaceAnalyzeRequest.isQueryPublic()) {
            // 全空间分析或者公共图库权限校验：仅管理员可访问

            ThrowUtils.throwIf(!userService.isAdmin(loginUser), ErrorCode.NO_AUTH_ERROR, "无权访问公共图库");
        } else {
            // 私有空间权限校验
            Long spaceId = spaceAnalyzeRequest.getSpaceId();
            ThrowUtils.throwIf(spaceId == null || spaceId <= 0, ErrorCode.PARAMS_ERROR);

            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
            spaceService.checkSpaceAuth(loginUser, space);
        }
    }

    @Override
    public List<SpaceCategoryAnalyzeResponse> getSpaceCategoryAnalyze(
        SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest, User loginUser) {
        ThrowUtils.throwIf(spaceCategoryAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);

        // 检查权限
        checkSpaceAnalyzeAuth(spaceCategoryAnalyzeRequest, loginUser);

        // 构造查询条件
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        // 根据分析范围补充查询条件
        fillAnalyzeQueryWrapper(spaceCategoryAnalyzeRequest, queryWrapper);

        // 使用 MyBatis-Plus 分组查询
        queryWrapper.select("category AS category", "COUNT(*) AS count", "SUM(picSize) AS totalSize")
            .groupBy("category");

        // 查询并转换结果
        return pictureService.getBaseMapper().selectMaps(queryWrapper).stream().map(result -> {
            String category = result.get("category") != null ? result.get("category").toString() : "未分类";
            Long count = ((Number)result.get("count")).longValue();
            Long totalSize = ((Number)result.get("totalSize")).longValue();
            return new SpaceCategoryAnalyzeResponse(category, count, totalSize);
        }).collect(Collectors.toList());
    }

    @Override
    public List<SpaceTagAnalyzeResponse> getSpaceTagAnalyze(SpaceTagAnalyzeRequest spaceTagAnalyzeRequest,
                                                            User loginUser) {
        ThrowUtils.throwIf(spaceTagAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);

        // 检查权限
        checkSpaceAnalyzeAuth(spaceTagAnalyzeRequest, loginUser);

        // 构造查询条件
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        fillAnalyzeQueryWrapper(spaceTagAnalyzeRequest, queryWrapper);

        // 查询所有符合条件的标签
        queryWrapper.select("tags");
        List<String> tagsJsonList = pictureService.getBaseMapper()
            .selectObjs(queryWrapper)
            .stream()
            .filter(ObjUtil::isNotNull)
            .map(Object::toString)
            .collect(Collectors.toList());

        // 合并所有标签并统计使用次数
        Map<String, Long> tagCountMap = tagsJsonList.stream()
            .flatMap(tagsJson -> JSONUtil.toList(tagsJson, String.class).stream())
            .collect(Collectors.groupingBy(tag -> tag, Collectors.counting()));

        // 转换为响应对象，按使用次数降序排序
        return tagCountMap.entrySet().stream().sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue())) // 降序排列
            .map(entry -> new SpaceTagAnalyzeResponse(entry.getKey(), entry.getValue())).collect(Collectors.toList());
    }

    @Override
    public List<SpaceSizeAnalyzeResponse> getSpaceSizeAnalyze(SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest,
                                                              User loginUser) {
        ThrowUtils.throwIf(spaceSizeAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);

        // 检查权限
        checkSpaceAnalyzeAuth(spaceSizeAnalyzeRequest, loginUser);

        // 构造查询条件
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        fillAnalyzeQueryWrapper(spaceSizeAnalyzeRequest, queryWrapper);

        // 查询所有符合条件的图片大小
        queryWrapper.select("picSize");
        List<Long> picSizes = pictureService.getBaseMapper()
            .selectObjs(queryWrapper)
            .stream()
            .map(size -> ((Number)size).longValue())
            .collect(Collectors.toList());

        // 定义分段范围，注意使用有序 Map
        AtomicInteger less100KB = new AtomicInteger();
        AtomicInteger less500KB = new AtomicInteger();
        AtomicInteger less1MB = new AtomicInteger();
        AtomicInteger greater1MB = new AtomicInteger();
        picSizes.parallelStream().forEach(size -> {
            if (size < 100 * 1024) {
                less100KB.getAndIncrement();
            } else if (size < 500 * 1024) {
                less500KB.getAndIncrement();
            } else if (size < 1 * 1024 * 1024) {
                less1MB.getAndIncrement();
            } else {
                greater1MB.getAndIncrement();
            }
        });

        Map<String, Long> sizeRanges = new LinkedHashMap<>();
        sizeRanges.put("<100KB", less100KB.longValue());
        sizeRanges.put("100KB-500KB", less500KB.longValue());
        sizeRanges.put("500KB-1MB", less1MB.longValue());
        sizeRanges.put(">1MB", greater1MB.longValue());

        // 转换为响应对象
        return sizeRanges.entrySet()
            .parallelStream()
            .map(entry -> new SpaceSizeAnalyzeResponse(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }

    @Override
    public List<SpaceUserAnalyzeResponse> getSpaceUserAnalyze(SpaceUserAnalyzeRequest spaceUserAnalyzeRequest,
                                                              User loginUser) {
        ThrowUtils.throwIf(spaceUserAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        // 检查权限
        checkSpaceAnalyzeAuth(spaceUserAnalyzeRequest, loginUser);

        // 构造查询条件
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        Long userId = spaceUserAnalyzeRequest.getUserId();
        queryWrapper.eq(ObjUtil.isNotNull(userId), "userId", userId);
        fillAnalyzeQueryWrapper(spaceUserAnalyzeRequest, queryWrapper);

        // 分析维度：每日、每周、每月
        String timeDimension = spaceUserAnalyzeRequest.getTimeDimension();
        switch (timeDimension) {
            case "day":
                queryWrapper.select("DATE_FORMAT(createTime, '%Y-%m-%d') AS period", "COUNT(*) AS count");
                break;
            case "week":
                queryWrapper.select("YEARWEEK(createTime) AS period", "COUNT(*) AS count");
                break;
            case "month":
                queryWrapper.select("DATE_FORMAT(createTime, '%Y-%m') AS period", "COUNT(*) AS count");
                break;
            default:
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的时间维度");
        }

        // 分组和排序
        queryWrapper.groupBy("period").orderByAsc("period");

        // 查询结果并转换
        List<Map<String, Object>> queryResult = pictureService.getBaseMapper().selectMaps(queryWrapper);
        return queryResult.parallelStream().map(result -> {
            String period = result.get("period").toString();
            Long count = ((Number)result.get("count")).longValue();
            return new SpaceUserAnalyzeResponse(period, count);
        }).collect(Collectors.toList());
    }

    @Override
    public List<Space> getSpaceRankAnalyze(SpaceRankAnalyzeRequest spaceRankAnalyzeRequest) {
        ThrowUtils.throwIf(spaceRankAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);

        // 取前 N 名
        // 查询结果
        return spaceMapper.getTopNSpaceUsage(spaceRankAnalyzeRequest.getTopN());
    }

}
