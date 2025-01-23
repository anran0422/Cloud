package com.anran.cloudlibrary.api.imagesearch;

import com.anran.cloudlibrary.api.imagesearch.model.ImageSearchResult;
import com.anran.cloudlibrary.api.imagesearch.sub.GetImageFirstUrlAPI;
import com.anran.cloudlibrary.api.imagesearch.sub.GetImageListAPI;
import com.anran.cloudlibrary.api.imagesearch.sub.GetImagePageAPI;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ImagSearchAPIFacade {
    /**
     * 搜索相似图片
     * 门面模式
     */
    public static List<ImageSearchResult> searchImage(String imageUrl) {
        String imagePageUrl = GetImagePageAPI.getImagePageUrl(imageUrl);
        String imageFirstUrl = GetImageFirstUrlAPI.getImageFirstUrl(imagePageUrl);
        List<ImageSearchResult> imageList = GetImageListAPI.getImageList(imageFirstUrl);
        return imageList;
    }

    public static void main(String[] args) {
        // 测试以图搜图功能
        String imageUrl = "https://www.codefather.cn/logo.png";
        List<ImageSearchResult> imageSearchResults = searchImage(imageUrl);
        System.out.println("结果列表：" + imageSearchResults);
    }
}
