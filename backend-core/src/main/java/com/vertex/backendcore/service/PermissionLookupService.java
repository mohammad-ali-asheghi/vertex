package com.vertex.backendcore.service;

import com.vertex.backendcore.entity.PermissionEntity;
import com.vertex.core.config.CommonConstant;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PermissionLookupService {

    @PersistenceContext
    private EntityManager entityManager;

    @Cacheable(
            value = CommonConstant.PERMISSIONS,
            key = "#role",
            unless = "#result == null || #result.isEmpty()"
    )
    public Map<String, Integer> getPermissionsMapByRole(String role) {
        log.debug("Fetching permissions from DB for role: {}", role);
        try {
            List<PermissionEntity> results = entityManager.createQuery(
                            "SELECT p FROM PermissionEntity p WHERE p.role = :role", PermissionEntity.class
                    )
                    .setParameter("role", role)
                    .getResultList();

            if (results.isEmpty()) {
                return Collections.emptyMap();
            }

            return results.stream()
                    .collect(Collectors.toMap(
                            p -> p.getMenuId().toString(),
                            PermissionEntity::getPermission,
                            (existing, replacement) -> existing
                    ));
        } catch (Exception e) {
            log.error("Error fetching permissions for role {}: {}", role, e.getMessage());
            return Collections.emptyMap();
        }
    }
}
