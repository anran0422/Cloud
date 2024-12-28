package com.anran.cloudlibrary.service;

import com.anran.cloudlibrary.model.VO.PictureVO;
import com.anran.cloudlibrary.model.dto.picture.PictureQueryRequest;
import com.anran.cloudlibrary.model.dto.picture.PictureReviewRequest;
import com.anran.cloudlibrary.model.dto.picture.PictureUploadByBatchRequest;
import com.anran.cloudlibrary.model.dto.picture.PictureUploadRequest;
import com.anran.cloudlibrary.model.entity.Picture;
import com.anran.cloudlibrary.model.entity.User;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

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

    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

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
}
