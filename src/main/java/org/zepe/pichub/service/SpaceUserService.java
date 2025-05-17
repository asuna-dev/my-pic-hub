package org.zepe.pichub.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.zepe.pichub.model.dto.spaceuser.SpaceUserAddRequest;
import org.zepe.pichub.model.dto.spaceuser.SpaceUserQueryRequest;
import org.zepe.pichub.model.entity.SpaceUser;
import com.baomidou.mybatisplus.extension.service.IService;
import org.zepe.pichub.model.vo.SpaceUserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author zzpus
 * @description 针对表【space_user(空间用户关联)】的数据库操作Service
 * @createDate 2025-05-17 00:28:37
 */
public interface SpaceUserService extends IService<SpaceUser> {

    long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest);

    void validSpaceUser(SpaceUser spaceUser, boolean add);

    QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);

    SpaceUserVO getSpaceUserVO(SpaceUser spaceUser, HttpServletRequest request);

    List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList);
}
