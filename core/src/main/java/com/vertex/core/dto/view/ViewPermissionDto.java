package com.vertex.core.dto.view;

import com.vertex.core.dto.base.BaseModel;
import com.vertex.core.enums.PermissionEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("unused")
@Getter
@Setter
public class ViewPermissionDto extends BaseModel implements Serializable {

    private String role;
    private Long menuId;
    private String menuTitle;
    private Integer permission;
    private String aliasName;
    private Long roleId;
    private List<PermissionEnum> permissions;
}
