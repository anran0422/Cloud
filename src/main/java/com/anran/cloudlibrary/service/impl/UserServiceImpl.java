package com.anran.cloudlibrary.service.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.anran.cloudlibrary.exception.BusinessException;
import com.anran.cloudlibrary.exception.ErrorCode;
import com.anran.cloudlibrary.exception.ThrowUtils;
import com.anran.cloudlibrary.manager.auth.StpKit;
import com.anran.cloudlibrary.mapper.UserMapper;
import com.anran.cloudlibrary.model.VO.LoginUserVO;
import com.anran.cloudlibrary.model.VO.UserVO;
import com.anran.cloudlibrary.model.dto.user.UserQueryRequest;
import com.anran.cloudlibrary.model.entity.User;
import com.anran.cloudlibrary.model.enums.UserRoleEnum;
import com.anran.cloudlibrary.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.anran.cloudlibrary.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author macbook
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2024-12-18 19:42:57
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码不可为空");
        }
        if (userAccount.length() < 5) throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不可小于 5 位");
        if (userPassword.length() < 8 || checkPassword.length() < 8)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不可小于 8 位");
        if (!userPassword.equals(checkPassword))
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入密码不一致");
        // 2. 检验账号是否重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        Long count = this.baseMapper.selectCount(queryWrapper);
        if (count > 0) throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复，请重新输入");
        // 3. 加密
        String encryptPassword = getEncryptPassword(userPassword);
        // 4. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("新用户");
        user.setUserRole(UserRoleEnum.USER.getValue());
        this.save(user);
        return user.getId();
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1、校验
        ThrowUtils.throwIf(StrUtil.hasBlank(userAccount, userPassword), ErrorCode.PARAMS_ERROR, "账号或密码为空");
        ThrowUtils.throwIf(userAccount.length() < 5, ErrorCode.PARAMS_ERROR, "账号小于 5 位");
        ThrowUtils.throwIf(userPassword.length() < 8, ErrorCode.PARAMS_ERROR, "密码小于 8 位");
        // 2、加密
        String encryptPassword = getEncryptPassword(userPassword);
        // 3、查询是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 如果不存在
        if (user == null) {
            log.error("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或账号密码错误");
        }
        // 4、记录用户的登录状态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        // todo 记录用户登录态到 Sa-token，便于空间鉴权时使用，注意保证该用户信息与 SpringSession 中的信息过期时间一致
        StpKit.SPACE.login(user.getId());
        StpKit.SPACE.getSession().set(USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user);
    }


    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR); // 可能登录态过期，但是还在原页面停留
        }
        // 查询是否存在（可能恰好被特殊处理了）
        // 追求性能可以注释 直接返回上面的结果
        Long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.SYSTEM_ERROR);
        // 先判断是否登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未登录");
        }
        // 移除登录状态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        StpKit.SPACE.logout();
        return true;
    }

    @Override
    public UserVO getUserVO(User user) {
        ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        ThrowUtils.throwIf(userList == null, ErrorCode.PARAMS_ERROR);

        if (CollectionUtils.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");

        // 拿到能够拼接的字符串
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();

        // 拼接条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);

        return queryWrapper;
    }

    @Override
    public boolean isAdmin(User user) {
        // 下边更加合理一些，按照 角色身份判断；我写的这个也合理
        //        return user != null && UserConstant.ADMIN_ROLE.equals(user.getUserRole());
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());

    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        // 校验
        if (user == null) return null;
        // 脱敏
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO); // 不存在的字段就会被过滤掉
        return loginUserVO;
    }

    @Override
    public String getEncryptPassword(String userPassword) {
        // 盐值，混淆密码
        final String SALT = "CloudLibrary";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }
}




