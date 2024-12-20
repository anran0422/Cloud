package com.anran.cloudlibrary.service;

import com.anran.cloudlibrary.model.VO.LoginUserVO;
import com.anran.cloudlibrary.model.VO.UserVO;
import com.anran.cloudlibrary.model.dto.user.UserQueryRequest;
import com.anran.cloudlibrary.model.entity.User;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author macbook
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2024-12-18 19:42:57
 */
public interface UserService extends IService<User> {

    /**
     * @param userAccount   账号
     * @param userPassword  密码
     * @param checkPassword 确认密码
     * @return 用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  账号
     * @param userPassword 密码
     * @param request      请求
     * @return 脱敏后的用户
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);


    /**
     * 获取脱敏后的用户
     *
     * @param user 用户
     * @return 脱敏后的用户
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获得加密密码
     *
     * @param userPassword 原始密码
     * @return 加密后的密码
     */
    String getEncryptPassword(String userPassword);

    /**
     * 获取当前登录用户
     *
     * @param request 请求
     * @return 当前用户
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 退出登录
     *
     * @param request 请求
     * @return 退出状态
     */
    boolean userLogout(HttpServletRequest request);

    UserVO getUserVO(User user);

    List<UserVO> getUserVOList(List<User> userList);

    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);
}
