package com.vertex.backendcore.entity;


import com.vertex.backendcore.entity.base.PO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("unused")
@Entity
@Table(
        name = "application",
        uniqueConstraints = {@UniqueConstraint(name = "uk_application_name", columnNames = {"name"})}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationEntity extends PO {

    /**
     * service-name
     */
    @Column(name = "name")
    @NotEmpty
    private String name;

    /**
     * Persian title
     */
    @Column(name = "title")
    @NotEmpty
    private String title;

    @Column(name = "url")
    @NotEmpty
    private String url;
}
