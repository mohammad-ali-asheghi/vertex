package com.vertex.backendcore.entity;

import com.vertex.backendcore.entity.base.PO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@SuppressWarnings("unused")
@Entity
@Table(name = "sec_users_roles")
@Data
@EqualsAndHashCode(callSuper = true)
public class UserRoleEntity extends PO implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            insertable = false,
            updatable = false
    )
    private UserEntity user;

    @NotNull
    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "role_id",
            insertable = false,
            updatable = false
    )
    private RoleEntity role;

    @NotNull
    @Column(name = "role_id")
    private Long roleId;
}