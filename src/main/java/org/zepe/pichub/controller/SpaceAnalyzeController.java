package org.zepe.pichub.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.zepe.pichub.annotation.AuthCheck;
import org.zepe.pichub.common.Response;
import org.zepe.pichub.constant.UserConstant;
import org.zepe.pichub.exception.ErrorCode;
import org.zepe.pichub.exception.ThrowUtils;
import org.zepe.pichub.model.dto.space.analyze.*;
import org.zepe.pichub.model.entity.Space;
import org.zepe.pichub.model.entity.User;
import org.zepe.pichub.model.enums.UserRoleEnum;
import org.zepe.pichub.model.vo.analyze.*;
import org.zepe.pichub.service.SpaceAnalyzeService;
import org.zepe.pichub.service.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author zzpus
 * @datetime 2025/4/28 15:10
 * @description
 */
@Slf4j
@RestController
@RequestMapping("/space/analyze")
public class SpaceAnalyzeController {

    @Resource
    private SpaceAnalyzeService spaceAnalyzeService;

    @Resource
    private UserService userService;

    /**
     * 获取空间使用状态
     */
    @PostMapping("/usage")
    public Response<SpaceUsageAnalyzeResponse> getSpaceUsageAnalyze(
        @RequestBody SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceUsageAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        SpaceUsageAnalyzeResponse spaceUsageAnalyze =
            spaceAnalyzeService.getSpaceUsageAnalyze(spaceUsageAnalyzeRequest, loginUser);
        return Response.success(spaceUsageAnalyze);
    }

    @PostMapping("/category")
    public Response<List<SpaceCategoryAnalyzeResponse>> getSpaceCategoryAnalyze(
        @RequestBody SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceCategoryAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        List<SpaceCategoryAnalyzeResponse> resultList =
            spaceAnalyzeService.getSpaceCategoryAnalyze(spaceCategoryAnalyzeRequest, loginUser);
        return Response.success(resultList);
    }

    @PostMapping("/tag")
    public Response<List<SpaceTagAnalyzeResponse>> getSpaceTagAnalyze(
        @RequestBody SpaceTagAnalyzeRequest spaceTagAnalyzeRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceTagAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        List<SpaceTagAnalyzeResponse> resultList =
            spaceAnalyzeService.getSpaceTagAnalyze(spaceTagAnalyzeRequest, loginUser);
        return Response.success(resultList);
    }

    @PostMapping("/size")
    public Response<List<SpaceSizeAnalyzeResponse>> getSpaceSizeAnalyze(
        @RequestBody SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceSizeAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        List<SpaceSizeAnalyzeResponse> resultList =
            spaceAnalyzeService.getSpaceSizeAnalyze(spaceSizeAnalyzeRequest, loginUser);
        return Response.success(resultList);
    }

    @PostMapping("/user")
    public Response<List<SpaceUserAnalyzeResponse>> getSpaceUserAnalyze(
        @RequestBody SpaceUserAnalyzeRequest spaceUserAnalyzeRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceUserAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        List<SpaceUserAnalyzeResponse> resultList =
            spaceAnalyzeService.getSpaceUserAnalyze(spaceUserAnalyzeRequest, loginUser);
        return Response.success(resultList);
    }

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/rank")
    public Response<List<Space>> getSpaceRankAnalyze(@RequestBody SpaceRankAnalyzeRequest spaceRankAnalyzeRequest,
                                                     HttpServletRequest request) {
        ThrowUtils.throwIf(spaceRankAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        // User loginUser = userService.getLoginUser(request);
        List<Space> resultList = spaceAnalyzeService.getSpaceRankAnalyze(spaceRankAnalyzeRequest);
        return Response.success(resultList);
    }

}
