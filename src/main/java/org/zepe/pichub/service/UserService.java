package org.zepe.pichub.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.zepe.pichub.model.dto.user.UserQueryRequest;
import org.zepe.pichub.model.dto.user.UserRegisterRequest;
import org.zepe.pichub.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.zepe.pichub.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author zzpus
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2025-04-28 00:30:04
 */
public interface UserService extends IService<User> {
    String getEncryptPassword(String userPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    UserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    LambdaQueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    boolean isAdmin(User user);

}
