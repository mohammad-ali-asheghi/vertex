package com.vertex.oauth.service;

import com.vertex.backendcore.repository.AbstractDAO;
import com.vertex.backendcore.service.AbstractCrudService;
import com.vertex.core.dto.MenuModel;
import com.vertex.core.dto.view.ViewMenuDto;
import com.vertex.core.util.PagedResponse;
import com.vertex.oauth.mapper.MenuMapper;
import com.vertex.oauth.repository.ViewMenuRepository;
import com.vertex.oauth.restriction.MenuRestriction;
import com.vertex.oauth.view.ViewMenu;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ViewMenuService extends AbstractCrudService<ViewMenu> {

    ViewMenuRepository viewMenuRepository;

    @PersistenceContext
    EntityManager entityManager;

    public ViewMenuService(
            AbstractDAO<ViewMenu> repository,
            PlatformTransactionManager ptm,
            ViewMenuRepository viewMenuRepository
    ) {
        super(repository, ptm);
        this.viewMenuRepository = viewMenuRepository;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    @Transactional(readOnly = true)
    public PagedResponse<ViewMenuDto> listInfo(MenuModel search, Pageable page, Boolean isMenuSearchKey) {
        MenuRestriction restriction = new MenuRestriction(search, isMenuSearchKey);
        Page<ViewMenu> entities = load(page, ViewMenu.class, restriction);
        return PagedResponse.ok(
                MenuMapper.get().toDto(entities.getContent()),
                entities.getTotalElements(),
                entities.hasNext()
        );
    }
}
