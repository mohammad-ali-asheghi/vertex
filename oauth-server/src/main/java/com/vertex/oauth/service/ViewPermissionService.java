package com.vertex.oauth.service;

import com.vertex.backendcore.repository.AbstractDAO;
import com.vertex.backendcore.service.AbstractCrudService;
import com.vertex.core.dto.view.ViewPermissionDto;
import com.vertex.oauth.mapper.ViewPermissionMapper;
import com.vertex.oauth.repository.ViewPermissionRepository;
import com.vertex.oauth.restriction.PermissionRestriction;
import com.vertex.oauth.view.ViewPermission;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ViewPermissionService extends AbstractCrudService<ViewPermission> {

    ViewPermissionRepository viewPermissionRepository;

    @PersistenceContext
    EntityManager entityManager;

    public ViewPermissionService(
            AbstractDAO<ViewPermission> repository,
            PlatformTransactionManager ptm,
            ViewPermissionRepository viewPermissionRepository
    ) {
        super(repository, ptm);
        this.viewPermissionRepository = viewPermissionRepository;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    @Transactional(readOnly = true)
    public List<ViewPermissionDto> getRelatedList(String role) {
        PermissionRestriction restriction = new PermissionRestriction(role);
        List<ViewPermission> entities = load(ViewPermission.class, restriction, null);

        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        return ViewPermissionMapper.get().toDto(entities);
    }
}
