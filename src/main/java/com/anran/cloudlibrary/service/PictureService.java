package com.anran.cloudlibrary.service;

import com.anran.cloudlibrary.model.VO.PictureVO;
import com.anran.cloudlibrary.model.dto.picture.*;
import com.anran.cloudlibrary.model.entity.Picture;
import com.anran.cloudlibrary.model.entity.User;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author macbook
 * @description 针对表【picture(图片)】的数据库操作Service
 * @createDate 2024-12-24 14:15:07
 */
public interface PictureService extends IService<Picture> {

    /**
     * 上传图片
     *
     * @param inputSource          上传文件类型
     * @param pictureUploadRequest 上传请求
     * @param loginUser            登录用户
     */
    PictureVO uploadPicture(Object inputSource,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);

    /**
     * 获取查询对象
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 获取查询对象包装类（单张）
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    /**
     * 获取查询对象包装类（分页）
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    /**
     * 校验图片
     */
    void validPicture(Picture picture);

    /**
     * 图片审核
     *
     * @param pictureReviewRequest 审核请求
     * @param loginUser            谁操作审核
     */
    void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);

    /**
     * 自动补充审核状态
     */
    void fillReviewParams(Picture picture, User loginUser);

    /**
     * 批量抓取和创建图片
     *
     * @param pictureUploadByBatchRequest 抓取请求
     * @param loginUser                   登录用户
     * @return 成功创建的图片数
     */
    Integer uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest,
                                 User loginUser);

    /**
     * 校验空间图片的权限
     */
    void checkPictureAuth(Picture picture, User loginUser);


    /**
     * 删除图片
     */
    void deletePicture(long pictureId, User loginUser);

    /**
     * 编辑图片
     */
    void editPicture(PictureEditRequest pictureEditRequest,
                     User loginUser);

    /**
     * 批量修改空间中的图片
     */
    @Transactional(rollbackFor = Exception.class)
    void editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest, User loginUser);

    /**
     * 根据主色调搜索图片
     */
    List<PictureVO> searchPictureByColor(Long spaceId, String picColor, User loginUser);
}
