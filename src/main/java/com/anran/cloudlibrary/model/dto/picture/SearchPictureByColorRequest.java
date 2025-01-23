package com.anran.cloudlibrary.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

@Data
public class SearchPictureByColorRequest implements Serializable {

    private static final long SerialVersionUID = 1L;

    /**
     * 空间ID
     */
    private Long spaceId;

    /**
     * 图片主色调
     */
    private String picColor;
}
