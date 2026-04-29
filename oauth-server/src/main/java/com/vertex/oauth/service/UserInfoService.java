package com.vertex.oauth.service;

import com.vertex.backendcore.entity.UserEntity;
import com.vertex.backendcore.entity.UserInfoEntity;
import com.vertex.backendcore.mapper.UserInfoMapper;
import com.vertex.backendcore.repository.AbstractDAO;
import com.vertex.backendcore.service.AbstractCrudService;
import com.vertex.backendcore.util.JsonUtil;
import com.vertex.core.dto.UserInfoModel;
import com.vertex.core.util.PagedResponse;
import com.vertex.oauth.repository.UserInfoRepository;
import com.vertex.oauth.restriction.UserInfoRestriction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserInfoService extends AbstractCrudService<UserInfoEntity> {

    UserInfoRepository userInfoRepository;
    private final UserService userService;

    @PersistenceContext
    EntityManager entityManager;

    public UserInfoService(
            AbstractDAO<UserInfoEntity> repository,
            PlatformTransactionManager ptm,
            UserService userService,
            UserInfoRepository userInfoRepository
    ) {
        super(repository, ptm);
        this.userService = userService;
        this.userInfoRepository = userInfoRepository;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    @Transactional(rollbackFor = Exception.class)
    public void createUserInfo(UserInfoModel.Update model, UserInfoModel.UserRecord record) {
        UserInfoEntity entity = UserInfoMapper.get().modelToEntity(model);
        insert(entity);
        userService.createUser(record, entity.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateUserInfo(UserInfoModel.Update model) {
        update(readForUpdate(UserInfoEntity.class, model.getId(), JsonUtil.toJson(model)));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteUserInfo(Long id) {
        delete(id);
    }

    @Transactional(readOnly = true)
    public UserInfoModel.Response getUserInfo(Long id) {
        UserInfoModel.Response model = UserInfoMapper
                .get()
                .entityToModel(findById(UserInfoEntity.class, id));
        fetchUserEntityInfo(model);
        return model;
    }

    private void fetchUserEntityInfo(UserInfoModel.Response model) {
        UserEntity entity = userService.getEntityManager().find(UserEntity.class, model.getId());
        model.setUsername(entity.getUsername());
        model.setActive(entity.isEnabled());
    }

    @Transactional(readOnly = true)
    public PagedResponse<UserInfoModel.Response> listInfo(UserInfoModel.Search search, Pageable page) {
        UserInfoRestriction restriction = new UserInfoRestriction(search);
        Page<UserInfoEntity> entities = load(page, UserInfoEntity.class, restriction);
        return PagedResponse.ok(
                UserInfoMapper.get().entitiesToModels(entities.getContent()),
                entities.getTotalElements(),
                entities.hasNext()
        );
    }
}
