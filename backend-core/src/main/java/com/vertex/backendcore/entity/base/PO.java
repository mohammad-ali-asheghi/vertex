package com.vertex.backendcore.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class PO extends BasePO {

    @Column(name = "create_user")
    @CreatedBy
    private String createdBy;

    @Column(name = "update_user")
    @LastModifiedBy
    private String updatedBy;

    @CreatedDate
    @Column(name = "create_date")
    private Date createdDate;

    @LastModifiedDate
    @Column(name = "update_date")
    private Date updatedDate;

    @Version
    @Column(name = "version", columnDefinition = " integer DEFAULT 0 ")
    private int version;
}
