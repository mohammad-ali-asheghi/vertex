package com.vertex.core.dto.view;

import com.vertex.core.dto.base.BaseModel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@SuppressWarnings("unused")
@Getter
@Setter
public class ViewMenuDto extends BaseModel implements Serializable {

    private String title;
    private String icon;
    private String searchKey;
    private Boolean active;
    private Long parentId;
    private Integer priority;
    private Long applicationId;
    private Boolean isSystemDataEntry;
    private Boolean hasChild;
}
