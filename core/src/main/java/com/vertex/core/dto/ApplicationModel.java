package com.vertex.core.dto;

import com.vertex.core.dto.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ApplicationModel extends BaseModel {
    private String name;
    private String title;
    private String url;
}

