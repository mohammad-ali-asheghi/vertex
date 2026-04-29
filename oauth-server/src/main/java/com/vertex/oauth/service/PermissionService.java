package com.vertex.oauth.service;

import com.vertex.backendcore.entity.PermissionEntity;
import com.vertex.backendcore.repository.AbstractDAO;
import com.vertex.backendcore.security.HttpUtil;
import com.vertex.backendcore.service.AbstractCrudService;
import com.vertex.core.config.CommonConstant;
import com.vertex.core.dto.PermissionModel;
import com.vertex.core.dto.interfaces.UnrelatedMenuProjection;
import com.vertex.core.enums.PermissionEnum;
import com.vertex.oauth.repository.PermissionRepository;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionService extends AbstractCrudService<PermissionEntity> {

    @PersistenceContext
    EntityManager entityManager;

    private final PermissionRepository permissionRepository;

    public PermissionService(
            AbstractDAO<PermissionEntity> repository,
            PlatformTransactionManager ptm,
            PermissionRepository permissionRepository
    ) {
        super(repository, ptm);
        this.permissionRepository = permissionRepository;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CommonConstant.PERMISSIONS, key = "#request.role()")
    public void syncRolePermissions(PermissionModel.Request request, SecretKey secretKey, String token) {
        var securityContext = extractSecurityContext(request, secretKey, token);
        var existingEntities = permissionRepository.getByRole(request.role());

        if (existingEntities == null || existingEntities.isEmpty()) {
            saveNewPermissions(securityContext, request.details());
        } else {
            syncExistingPermissions(securityContext, request.details(), existingEntities);
        }
    }

    private void saveNewPermissions(PermissionModel.Request context, List<PermissionModel.Detail> details) {
        var entities = details.stream()
                .map(detail -> createEntity(detail, context))
                .toList();
        insert(entities);
    }

    private void syncExistingPermissions(
            PermissionModel.Request context,
            List<PermissionModel.Detail> details,
            List<PermissionEntity> existingEntities
    ) {
        var entityMap = existingEntities.stream()
                .collect(Collectors.toMap(PermissionEntity::getMenuId, e -> e));

        var toDelete = new ArrayList<Long>();
        var toUpdate = new ArrayList<PermissionEntity>();
        var toCreate = new ArrayList<PermissionEntity>();

        for (var detail : details) {
            var entity = entityMap.remove(detail.menuId());

            if (entity != null) {
                if (detail.permissions() == null || detail.permissions().isEmpty()) {
                    toDelete.add(entity.getId());
                } else {//may be improved change user ip and device info when update
                    entity.setPermission(PermissionEnum.calculateMask(detail.permissions()));
                    toUpdate.add(entity);
                }
            } else {
                toCreate.add(createEntity(detail, context));
            }
        }

        entityMap.values().forEach(e -> toDelete.add(e.getId()));

        if (!toDelete.isEmpty()) delete(toDelete);
        if (!toUpdate.isEmpty()) update(toUpdate);
        if (!toCreate.isEmpty()) insert(toCreate);
    }

    private PermissionEntity createEntity(PermissionModel.Detail detail, PermissionModel.Request context) {
        var entity = new PermissionEntity();
        entity.setMenuId(detail.menuId());
        entity.setRole(context.role());
        entity.setPermission(PermissionEnum.calculateMask(detail.permissions()));

        entity.setDevice(context.device());
        entity.setUserIp(context.userIp());

        entity.setCreatedBy(context.createdBy());
        return entity;
    }

    private PermissionModel.Request extractSecurityContext(PermissionModel.Request request, SecretKey secretKey, String token) {
        var claims = getClaims(token, secretKey);
        return PermissionModel.Request.builder()
                .role(request.role())
                .createdBy(claims.getSubject())
                .device(claims.get("userAgent", String.class))
                .userIp(claims.get("ipAddress", String.class))
                .build();
    }

    private Claims getClaims(String token, SecretKey secretKey) {
        String cleanToken = HttpUtil.extractToken(token);
        return HttpUtil.parseToken(cleanToken, secretKey).getPayload();
    }

    @Transactional(readOnly = true)
    public List<UnrelatedMenuProjection> getUnrelatedMenus(String role) {
        return permissionRepository.getUnrelatedMenusByRole(role);
    }
}
