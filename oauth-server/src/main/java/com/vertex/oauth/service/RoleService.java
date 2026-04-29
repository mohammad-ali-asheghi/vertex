package com.vertex.oauth.service;

import com.vertex.backendcore.entity.RoleEntity;
import com.vertex.backendcore.mapper.RoleMapper;
import com.vertex.backendcore.repository.AbstractDAO;
import com.vertex.backendcore.service.AbstractCrudService;
import com.vertex.backendcore.util.JsonUtil;
import com.vertex.core.dto.RoleModel;
import com.vertex.core.util.PagedResponse;
import com.vertex.oauth.config.OauthConstant;
import com.vertex.oauth.restriction.RoleRestriction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleService extends AbstractCrudService<RoleEntity> {

    @PersistenceContext
    EntityManager entityManager;

    public RoleService(
            AbstractDAO<RoleEntity> repository,
            PlatformTransactionManager ptm
    ) {
        super(repository, ptm);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    @Transactional(rollbackFor = Exception.class)
    public void createRole(RoleModel model) {
        ensureRolePrefix(model);
        insert(RoleMapper.get().modelToEntity(model));
    }

    private void ensureRolePrefix(RoleModel model) {
        if (!model.getName().startsWith(OauthConstant.ROLE_PREFIX)) {
            model.setName(OauthConstant.ROLE_PREFIX + model.getName());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateRole(RoleModel model) {
        ensureRolePrefix(model);
        update(readForUpdate(RoleEntity.class, model.getId(), JsonUtil.toJson(model)));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        delete(id);
    }

    @Transactional(readOnly = true)
    public RoleModel getRole(Long id) {
        return RoleMapper.get().entityToModel(findById(RoleEntity.class, id));
    }

    @Transactional(readOnly = true)
    public PagedResponse<RoleModel> listInfo(RoleModel search, Pageable page) {
        RoleRestriction restriction = new RoleRestriction(search);
        Page<RoleEntity> entities = load(page, RoleEntity.class, restriction);
        return PagedResponse.ok(
                RoleMapper.get().entitiesToModels(entities.getContent()),
                entities.getTotalElements(),
                entities.hasNext()
        );
    }
}
