package com.anran.cloudlibrary.controller;

import com.anran.cloudlibrary.annotation.AuthCheck;
import com.anran.cloudlibrary.common.BaseResponse;
import com.anran.cloudlibrary.common.DeleteRequest;
import com.anran.cloudlibrary.common.ResultUtils;
import com.anran.cloudlibrary.constant.UserConstant;
import com.anran.cloudlibrary.exception.BusinessException;
import com.anran.cloudlibrary.exception.ErrorCode;
import com.anran.cloudlibrary.exception.ThrowUtils;
import com.anran.cloudlibrary.model.VO.LoginUserVO;
import com.anran.cloudlibrary.model.VO.UserVO;
import com.anran.cloudlibrary.model.dto.user.*;
import com.anran.cloudlibrary.model.entity.User;
import com.anran.cloudlibrary.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     *
     * @param userRegisterRequest 注册请求参数
     * @return 用户 id
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);

        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();

        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求
     * @param request          请求
     * @return 脱敏后的用户
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(request == null, ErrorCode.SYSTEM_ERROR);

        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 获取当前登录用户
     *
     * @param request 请求
     * @return 脱敏后的用户
     */
    @GetMapping("/get/current")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.SYSTEM_ERROR);
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(loginUser));
    }

    /**
     * 退出登录
     *
     * @param request 请求
     * @return 退出后的结果
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> logout(HttpServletRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.SYSTEM_ERROR);
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 创建用户（仅管理员）
     *
     * @param userAddRequest 添加用户请求参数
     * @return 用户 id
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        ThrowUtils.throwIf(userAddRequest == null, ErrorCode.SYSTEM_ERROR);

        // todo 自己加的逻辑很棒
        // 管理员设置账号 也要保证规则 < 5 位
        ThrowUtils.throwIf(userAddRequest.getUserAccount().length() < 5, ErrorCode.SYSTEM_ERROR, "账号小于 5 位");

        // 查询账号是否重复
        String userAccount = userAddRequest.getUserAccount();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        User repeatUser = userService.getBaseMapper().selectOne(queryWrapper);
        ThrowUtils.throwIf(repeatUser != null, ErrorCode.PARAMS_ERROR, "账号重复，请更换账号！");

        // 创建用户
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        // 用户其他的默认参数（数据库不强调空值）
        String DEFAULT_PASSWORD = "12345678";
        String encryptPassword = userService.getEncryptPassword(DEFAULT_PASSWORD);
        user.setUserPassword(encryptPassword);
        // 保存返回结果
        boolean save = userService.save(user);
        ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());
    }

    /**
     * 根据 id 获取用户（仅管理员）
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(Long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * 根据 id 获取 脱敏后的用户
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(Long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 利用了上边管理员的方法，response-> data code message!
        BaseResponse<User> response = getUserById(id);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 删除用户
     *
     * @param deleteRequest 删除请求
     * @return 删除结果
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        boolean result = userService.removeById(deleteRequest.getId());
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR); // 加了之后结果必为 true
        return ResultUtils.success(true);
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest 更新请求参数
     * @return 更新结果
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null || userUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 分页获取用户封装列表（仅管理员）
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 分页插件构建分页
        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, pageSize),
                userService.getQueryWrapper(userQueryRequest));
        // 创建页面容纳 userVOList
        Page<UserVO> userVOPage = new Page<>(current, pageSize, userPage.getTotal());
        List<UserVO> userVOList = userService.getUserVOList(userPage.getRecords());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }
}
