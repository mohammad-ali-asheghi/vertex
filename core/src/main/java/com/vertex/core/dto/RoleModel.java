package com.vertex.core.dto;

import com.vertex.core.dto.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@SuppressWarnings("unused")
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleModel extends BaseModel {

    private String name;
    private String description;
    private String aliasName;
    private boolean active;
    private Long applicationId;
    private ApplicationModel application;
}
