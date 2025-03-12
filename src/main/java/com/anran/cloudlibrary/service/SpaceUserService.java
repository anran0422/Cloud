package com.anran.cloudlibrary.service;

import com.anran.cloudlibrary.model.VO.SpaceUserVO;
import com.anran.cloudlibrary.model.dto.spaceuser.SpaceUserAddRequest;
import com.anran.cloudlibrary.model.dto.spaceuser.SpaceUserQueryRequest;
import com.anran.cloudlibrary.model.entity.SpaceUser;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author macbook
 * @description 针对表【space_user(空间用户关联)】的数据库操作Service
 * @createDate 2025-02-27 21:00:48
 */
public interface SpaceUserService extends IService<SpaceUser> {

    long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest);

    QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);

    SpaceUserVO getSpaceUserVO(SpaceUser spaceUser, HttpServletRequest request);

    List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList);

    void validSpaceUser(SpaceUser spaceUser, boolean add);
}
