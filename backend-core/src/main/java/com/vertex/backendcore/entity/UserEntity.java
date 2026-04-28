package com.vertex.backendcore.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vertex.backendcore.annotation.UniqueUsername;
import com.vertex.backendcore.entity.base.BasePO;
import com.vertex.backendcore.enums.TypeLastUpdateEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
@Entity
@Table(name = "sec_user")
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserEntity extends BasePO implements Serializable, UserDetails {

    @UniqueUsername
    @Column(name = "username")
    @NotEmpty
    private String username;

    @Column(name = "password")
    @NotEmpty
    private String password;

    @NotNull
    @Column(name = "is_enabled")
    private boolean enabled;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @JoinColumn(name = "user_info_id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private UserInfoEntity userInfo;

    @Column(name = "user_info_id", nullable = false)
    private Long userInfoId;

    @ManyToMany(targetEntity = RoleEntity.class, fetch = FetchType.EAGER)
    @JoinTable(
            name = "sec_users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles = new HashSet<>();

    @Column(name = "password_expire_date")
    private LocalDateTime passwordExpireDate;

    @Column(name = "login_attempt_number")
    private Integer loginAttemptNumber;

    @Column(name = "unlock_date")
    private LocalDateTime unlockDate;

    @Column(name = "expire_date")
    private LocalDateTime expireDate;

    @Column(name = "last_update")
    @Enumerated(EnumType.STRING)
    private TypeLastUpdateEnum lastUpdate;

    public String getFirstName() {
        return getUserInfo().getFirstName();
    }

    public String getLastName() {
        return getUserInfo().getLastName();
    }

    public String getNationalCode() {
        return getUserInfo().getNationalNo();
    }

    @Override
    public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public boolean isAccountNonExpired() {
        return getExpireDate() == null || LocalDateTime.now().isBefore(getExpireDate());
    }

    @Override
    public boolean isAccountNonLocked() {
        return getLoginAttemptNumber() == null || getLoginAttemptNumber() < 6;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return getPasswordExpireDate() == null || LocalDateTime.now().isBefore(getPasswordExpireDate());
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public @NonNull String getUsername() {
        return this.username;
    }
}
