package org.zepe.pichub.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.zepe.pichub.model.dto.space.SpaceAddRequest;
import org.zepe.pichub.model.dto.space.SpaceQueryRequest;
import org.zepe.pichub.model.entity.Space;
import com.baomidou.mybatisplus.extension.service.IService;
import org.zepe.pichub.model.entity.User;
import org.zepe.pichub.model.vo.SpaceVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zzpus
 * @description 针对表【space(空间)】的数据库操作Service
 * @createDate 2025-05-14 14:22:49
 */
public interface SpaceService extends IService<Space> {
    long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);

    void validSpace(Space space, boolean add);

    void fillSpaceBySpaceLevel(Space space);

    void checkSpaceAuth(User loginUser, Space space);

    /**
     * 获取空间包装类（单条）
     *
     * @param space
     * @param request
     * @return
     */
    SpaceVO getSpaceVO(Space space);

    /**
     * 获取空间包装类（分页）
     *
     * @param spacePage
     * @param request
     * @return
     */
    Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request);

    /**
     * 获取查询对象
     *
     * @param spaceQueryRequest
     * @return
     */
    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);

}
