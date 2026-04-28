package com.vertex.backendcore.service;

import com.vertex.backendcore.repository.AbstractDAO;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("unused")
@Transactional
public abstract class AbstractCrudService<E> extends AbstractEntityService<E> {

    public AbstractCrudService(AbstractDAO<E> repository, PlatformTransactionManager ptm) {
        super(repository, ptm);
    }
}
