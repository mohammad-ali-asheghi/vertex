package com.vertex.oauth.service;

import com.vertex.backendcore.entity.UserRoleEntity;
import com.vertex.backendcore.repository.AbstractDAO;
import com.vertex.backendcore.service.AbstractCrudService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

@Service
public class UserRoleService extends AbstractCrudService<UserRoleEntity> {


    @PersistenceContext
    EntityManager entityManager;

    public UserRoleService(
            AbstractDAO<UserRoleEntity> repository,
            PlatformTransactionManager ptm
    ) {
        super(repository, ptm);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }
}
