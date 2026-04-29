package com.vertex.oauth.view;

import com.vertex.backendcore.entity.base.BasePO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

@Getter
@Setter
@Entity
@Table(name = "view_permission")
@Immutable
public class ViewPermission extends BasePO {

    @Column(name = "role")
    private String role;

    @Column(name = "menu_id")
    private Long menuId;

    @Column(name = "menu_title")
    private String menuTitle;

    @Column(name = "permission")
    private Integer permission;

    @Column(name = "alias_name")
    private String aliasName;

    @Column(name = "role_id")
    private Long roleId;
}
