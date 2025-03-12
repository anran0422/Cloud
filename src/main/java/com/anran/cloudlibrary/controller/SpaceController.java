package com.anran.cloudlibrary.controller;

import cn.hutool.core.bean.BeanUtil;
import com.anran.cloudlibrary.annotation.AuthCheck;
import com.anran.cloudlibrary.common.BaseResponse;
import com.anran.cloudlibrary.common.DeleteRequest;
import com.anran.cloudlibrary.common.ResultUtils;
import com.anran.cloudlibrary.constant.UserConstant;
import com.anran.cloudlibrary.exception.BusinessException;
import com.anran.cloudlibrary.exception.ErrorCode;
import com.anran.cloudlibrary.exception.ThrowUtils;
import com.anran.cloudlibrary.manager.auth.SpaceUserAuthManager;
import com.anran.cloudlibrary.model.VO.SpaceVO;
import com.anran.cloudlibrary.model.dto.space.*;
import com.anran.cloudlibrary.model.entity.Space;
import com.anran.cloudlibrary.model.entity.User;
import com.anran.cloudlibrary.model.enums.SpaceLevelEnum;
import com.anran.cloudlibrary.service.SpaceService;
import com.anran.cloudlibrary.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/space")
public class SpaceController {

    @Resource
    private UserService userService;

    @Resource
    private SpaceService spaceService;

    @Resource
    private SpaceUserAuthManager spaceUserAuthManager;


    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addSpace(@RequestBody SpaceAddRequest spaceAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceAddRequest == null, ErrorCode.PARAMS_ERROR);

        User loginUser = userService.getLoginUser(request);
        long spaceId = spaceService.addSpace(spaceAddRequest, loginUser);
        return ResultUtils.success(spaceId);
    }

    /**
     * 删除空间
     *
     * @param request 有需要登录态等，就需要request参数
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteSpace(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long spaceId = deleteRequest.getId();
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        Space space = spaceService.getById(spaceId);
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人和管理员可以删除
        spaceService.checkSpaceAuth(loginUser, space);

        // 操作数据库
        boolean res = spaceService.removeById(spaceId);
        ThrowUtils.throwIf(!res, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新空间（仅管理员）
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateSpace(@RequestBody SpaceUpdateRequest spaceUpdateRequest,
                                             HttpServletRequest request) {
        if (spaceUpdateRequest == null || spaceUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 将实体类和 DTO 转换 描述不严谨
        // 赋值给数据库 类容器
        Space space = new Space();
        BeanUtil.copyProperties(spaceUpdateRequest, space);
        // 自动填充数据
        spaceService.fillSpaceBySpaceLevel(space);

        // 校验更新后的空间
        spaceService.validSpace(space, false);
        // 判断原本的空间是否还存在
        Long id = spaceUpdateRequest.getId();
        Space oldSpace = spaceService.getById(id);
        ThrowUtils.throwIf(oldSpace == null, ErrorCode.NOT_FOUND_ERROR);

        // 操作数据库
        boolean res = spaceService.updateById(space);
        ThrowUtils.throwIf(!res, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取空间（仅管理员可用）
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Space> getSpaceById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Space space = spaceService.getById(id);
        ThrowUtils.throwIf(space == null, ErrorCode.PARAMS_ERROR);
        // 返回空间
        return ResultUtils.success(space);
    }

    /**
     * 根据 id 获取空间（封装类)
     */
    @GetMapping("/get/vo")
    public BaseResponse<SpaceVO> getSpaceVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Space space = spaceService.getById(id);
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR);
        SpaceVO spaceVO = spaceService.getSpaceVO(space, request);
        User loginUser = userService.getLoginUser(request);
        List<String> permissionList = spaceUserAuthManager.getPermissionList(space, loginUser);
        spaceVO.setPermissionList(permissionList);

        // 返回空间
        return ResultUtils.success(spaceVO);
    }

    /**
     * 分页获取空间列表（仅管理员）
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Space>> listSpaceByPage(@RequestBody SpaceQueryRequest spaceQueryRequest) {
        // todo 允许为空，那就是查询全部；因为这里不是请求，而是筛选条件
//        ThrowUtils.throwIf(spaceQueryRequest == null, ErrorCode.PARAMS_ERROR);
        int current = spaceQueryRequest.getCurrent();
        int pageSize = spaceQueryRequest.getPageSize();
        // 通过查询条件 查询数据库
        Page<Space> spacePage = spaceService.page(new Page<>(current, pageSize),
                spaceService.getQueryWrapper(spaceQueryRequest));
        return ResultUtils.success(spacePage);
    }

    /**
     * 分页获取空间列表（封装类）
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<SpaceVO>> listSpaceVOByPage(@RequestBody SpaceQueryRequest spaceQueryRequest,
                                                         HttpServletRequest request) {

        // todo 允许为空，那就是查询全部；因为 QueryRequest 不是请求，而是筛选条件
//        ThrowUtils.throwIf(spaceQueryRequest == null, ErrorCode.PARAMS_ERROR);
        int current = spaceQueryRequest.getCurrent();
        int pageSize = spaceQueryRequest.getPageSize();

        // 限制爬虫
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        // 通过查询条件 查询数据库
        Page<Space> spacePage = spaceService.page(new Page<>(current, pageSize),
                spaceService.getQueryWrapper(spaceQueryRequest));
        return ResultUtils.success(spaceService.getSpaceVOPage(spacePage, request));
    }


    /**
     * 编辑空间（给用户使用）
     * 类似于更新接口
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editSpace(@RequestBody SpaceEditRequest spaceEditRequest,
                                           HttpServletRequest request) {
        if ((spaceEditRequest == null) || (spaceEditRequest.getId() <= 0)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 赋值给数据库 类容器
        Space space = new Space();
        BeanUtil.copyProperties(spaceEditRequest, space);
        // 设置编辑时间
        space.setEditTime(new Date());
        // 校验更新后的空间
        spaceService.validSpace(space, false);
        // 判断原本的空间还在不在
        Long id = spaceEditRequest.getId();
        Space oldSpace = spaceService.getById(id);
        ThrowUtils.throwIf(oldSpace == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人和管理员可以编辑
        User loginUser = userService.getLoginUser(request);
        spaceService.checkSpaceAuth(loginUser, oldSpace);

        // 补充审核参数
        spaceService.fillSpaceBySpaceLevel(space);
        // 操作数据库
        boolean res = spaceService.updateById(space);
        ThrowUtils.throwIf(!res, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(true);
    }

    @GetMapping("/list/level")
    public BaseResponse<List<SpaceLevel>> listSpaceLevel() {
        List<SpaceLevel> spaceLevelList = Arrays.stream(SpaceLevelEnum.values())
                .map(spaceLevelEnum -> new SpaceLevel(
                        spaceLevelEnum.getValue(),
                        spaceLevelEnum.getText(),
                        spaceLevelEnum.getMaxCount(),
                        spaceLevelEnum.getMaxSize()))
                .collect(Collectors.toList());

        return ResultUtils.success(spaceLevelList);
    }
}
