package com.vertex.backendcore.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vertex.backendcore.entity.base.PO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@SuppressWarnings("unused")
@Entity
@Table(
        name = "sec_role",
        uniqueConstraints = {@UniqueConstraint(name = "uk_role_name", columnNames = {"name"})}
)
@Getter
@Setter
public class RoleEntity extends PO implements GrantedAuthority {

    @Column(name = "name")
    @NotEmpty
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "alias_name")
    @NotEmpty
    private String aliasName;

    @Column(name = "is_active")
    private boolean active;

    @Column(name = "application_id")
    private Long applicationId;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @JoinColumn(name = "application_id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ApplicationEntity application;

    @Override
    public String getAuthority() {
        return getName();
    }
}
