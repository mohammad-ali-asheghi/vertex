package com.vertex.oauth.service;

import com.vertex.backendcore.entity.ApplicationEntity;
import com.vertex.backendcore.mapper.ApplicationMapper;
import com.vertex.backendcore.repository.AbstractDAO;
import com.vertex.backendcore.service.AbstractCrudService;
import com.vertex.core.dto.ApplicationModel;
import com.vertex.core.util.PagedResponse;
import com.vertex.oauth.repository.ApplicationRepository;
import com.vertex.oauth.restriction.ApplicationRestriction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplicationService extends AbstractCrudService<ApplicationEntity> {

    ApplicationRepository applicationRepository;

    @PersistenceContext
    EntityManager entityManager;

    public ApplicationService(
            AbstractDAO<ApplicationEntity> repository,
            PlatformTransactionManager ptm,
            ApplicationRepository applicationRepository
    ) {
        super(repository, ptm);
        this.applicationRepository = applicationRepository;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    @Transactional(readOnly = true)
    public ApplicationModel getApplication(Long id) {
        return ApplicationMapper.get().entityToModel(findById(ApplicationEntity.class, id));
    }

    @Transactional(readOnly = true)
    public PagedResponse<ApplicationModel> listInfo(ApplicationModel search, Pageable pageable) {
        ApplicationRestriction restriction = new ApplicationRestriction(search);
        Page<ApplicationEntity> entities = load(pageable, ApplicationEntity.class, restriction);
        return PagedResponse.ok(
                ApplicationMapper.get().entitiesToModels(entities.getContent()),
                entities.getTotalElements(),
                entities.hasNext()
        );
    }
}
