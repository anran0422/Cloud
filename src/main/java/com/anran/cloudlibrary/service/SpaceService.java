package com.anran.cloudlibrary.service;

import com.anran.cloudlibrary.model.VO.SpaceVO;
import com.anran.cloudlibrary.model.dto.space.SpaceAddRequest;
import com.anran.cloudlibrary.model.dto.space.SpaceQueryRequest;
import com.anran.cloudlibrary.model.entity.Space;
import com.anran.cloudlibrary.model.entity.User;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
 * @author macbook
 * @description 针对表【space(空间)】的数据库操作Service
 * @createDate 2025-01-05 21:13:48
 */
public interface SpaceService extends IService<Space> {

    /**
     * 获取查询对象
     */
    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);

    /**
     * 获取查询对象包装类（单张）
     */
    SpaceVO getSpaceVO(Space space, HttpServletRequest request);

    /**
     * 获取查询对象包装类（分页）
     */
    Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request);

    /**
     * 校验空间
     */
    void validSpace(Space space, boolean add);

    /**
     * 根据空间级别填充空间限额
     */
    void fillSpaceBySpaceLevel(Space space);

    long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);
    
    void checkSpaceAuth(User loginUser, Space space);
}
