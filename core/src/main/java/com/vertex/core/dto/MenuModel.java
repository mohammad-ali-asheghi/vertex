package com.vertex.core.dto;

import com.vertex.core.dto.base.BaseModel;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("unused")
@Getter
@Setter
public class MenuModel extends BaseModel {

    private String title;
    private String icon;
    private String searchKey;
    private String description;
    private Integer priority;
    private Boolean active;
    private Boolean isSystemDataEntry;
    private MenuModel parent;
    private Long parentId;
    private Boolean hasChild;
}