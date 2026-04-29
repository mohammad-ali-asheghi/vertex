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
@Table(name = "view_sec_menu")
@Immutable
public class ViewMenu extends BasePO {

    @Column(name = "title")
    private String title;

    @Column(name = "icon")
    private String icon;

    @Column(name = "search_key")
    private String searchKey;

    @Column(name = "is_active")
    private Boolean active;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "application_id")
    private Long applicationId;

    @Column(name = "is_system_data_entry")
    private Boolean isSystemDataEntry;

    @Column(name = "has_child")
    private Boolean hasChild;
}
