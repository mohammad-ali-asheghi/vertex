package com.vertex.backendcore.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vertex.backendcore.entity.base.PO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("unused")
@Entity
@Table(name = "sec_menu")
@Getter
@Setter
public class MenuEntity extends PO implements Comparable<MenuEntity> {

    @Column(name = "title")
    @NotEmpty
    private String title;

    @Column(name = "icon")
    private String icon;

    @Column(name = "search_key")
    @NotEmpty
    private String searchKey;

    @Column(name = "description")
    private String description;

    @Column(name = "is_active")
    private boolean active;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private MenuEntity parent;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "application_id")
    private Long applicationId;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @JoinColumn(name = "application_id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ApplicationEntity application;

    @Column(name = "is_system_data_entry")
    private Boolean isSystemDataEntry;

    public static String getAppTitle(ApplicationEntity app) {
        return app.getTitle();
    }

    public static String getParentTitle(MenuEntity parent) {
        return parent.getTitle();
    }

    @Override
    public int compareTo(MenuEntity o) {
        return getPriority().compareTo(o.getPriority());
    }

}
