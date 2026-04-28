package com.vertex.backendcore.entity;

import com.vertex.backendcore.entity.base.BasePO;
import com.vertex.core.enums.PermissionEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("unused")
@Entity()
@Table(name = "permission")
@Setter
@Getter
public class PermissionEntity extends BasePO {

    @NotNull
    @Column(name = "role", nullable = false)
    private String role;

    @NotNull
    @Column(name = "menu_id", nullable = false)
    private Long menuId;

    @NotNull
    @Column(name = "permission", nullable = false)
    private Integer permission = 0;

    @Column(name = "create_user")
    private String createdBy;

    @Column(name = "device")
    private String device;

    @Column(name = "user_ip")
    private String userIp;

    public boolean hasPermission(PermissionEnum type) {
        return (this.permission & type.getMask()) == type.getMask();
    }
}
