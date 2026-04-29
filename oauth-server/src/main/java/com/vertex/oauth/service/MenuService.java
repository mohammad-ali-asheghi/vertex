package com.vertex.oauth.service;

import com.vertex.backendcore.entity.MenuEntity;
import com.vertex.backendcore.repository.AbstractDAO;
import com.vertex.backendcore.service.AbstractCrudService;
import com.vertex.core.dto.MenuModel;
import com.vertex.oauth.mapper.MenuMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MenuService extends AbstractCrudService<MenuEntity> {

    @PersistenceContext
    EntityManager entityManager;

    public MenuService(
            AbstractDAO<MenuEntity> repository,
            PlatformTransactionManager ptm
    ) {
        super(repository, ptm);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateMenu(MenuModel record) {
        MenuEntity entity = findById(MenuEntity.class, record.getId());
        entity.setTitle(record.getTitle());
        entity.setActive(record.getActive());
        entity.setIsSystemDataEntry(false);
        entity.setPriority(record.getPriority());
        entity.setIcon(record.getIcon());
        entity.setDescription(record.getDescription());
        update(entity);
    }

    @Transactional(readOnly = true)
    public MenuModel getMenu(Long id) {
        return MenuMapper.get().toModel(findById(MenuEntity.class, id));
    }
}
