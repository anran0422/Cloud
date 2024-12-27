package com.anran.cloudlibrary.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class PictureTagCategory implements Serializable {

    private List<String> tagList;

    private List<String> categoryList;
}
