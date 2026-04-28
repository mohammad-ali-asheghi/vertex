package com.vertex.backendcore.service;

import com.vertex.backendcore.mapper.PermissionMapper;
import com.vertex.core.enums.PermissionEnum;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;

@Slf4j
@Component
public class PermissionEvaluatorService implements PermissionEvaluator {

    private PermissionLookupService permissionLookupService;

    @Autowired
    public void setPermissionLookupService(PermissionLookupService permissionLookupService) {
        this.permissionLookupService = permissionLookupService;
    }

    @Override
    public boolean hasPermission(
            @NonNull Authentication auth,
            @NonNull Object targetDomainObject,
            @NonNull Object permission
    ) {
        Permission springPermission;
        try {
            if (permission instanceof PermissionEnum) {
                springPermission = PermissionMapper.toSpringPermission((PermissionEnum) permission);
            } else {
                springPermission = (Permission) permission;
            }
        } catch (Exception e) {
            log.error("Error When Casting Permission With Message : {}", e.getMessage());
            return false;
        }

        if (!(targetDomainObject instanceof Number menuId)) {
            return false;
        }

        try {
            return auth.getAuthorities().stream()
                    .anyMatch(authority ->
                            hasUserPermission(authority.getAuthority(), menuId.toString(), springPermission)
                    );
        } catch (IllegalArgumentException e) {
            log.warn("Error evaluating permission: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean hasPermission(
            @NonNull Authentication auth,
            @NonNull Serializable targetId,
            @NonNull String targetType,
            @NonNull Object permission
    ) {
        return false;
    }

    public boolean hasUserPermission(String role, String menuId, Permission requiredPermission) {
        Map<String, Integer> rolePermissions = permissionLookupService.getPermissionsMapByRole(role);

        if (rolePermissions == null || !rolePermissions.containsKey(menuId)) {
            return false;
        }

        Integer mask = rolePermissions.get(menuId);
        int requiredMaskValue = requiredPermission.getMask();

        return (mask & requiredMaskValue) != 0;
    }
}