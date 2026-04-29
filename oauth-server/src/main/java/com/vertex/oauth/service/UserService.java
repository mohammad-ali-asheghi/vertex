package com.vertex.oauth.service;

import com.vertex.backendcore.entity.UserEntity;
import com.vertex.backendcore.enums.TypeLastUpdateEnum;
import com.vertex.backendcore.repository.AbstractDAO;
import com.vertex.backendcore.service.AbstractCrudService;
import com.vertex.core.dto.UserInfoModel;
import com.vertex.oauth.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

@Service
public class UserService extends AbstractCrudService<UserEntity> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PersistenceContext
    EntityManager entityManager;

    public UserService(
            AbstractDAO<UserEntity> repository,
            PlatformTransactionManager ptm,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        super(repository, ptm);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public void createUser(UserInfoModel.UserRecord record, Long userInfoId) {
        UserEntity entity = new UserEntity();
        entity.setPassword(passwordEncoder.encode(record.password()));
        entity.setUsername(record.username());
        entity.setEnabled(true);
        entity.setLoginAttemptNumber(0);
        entity.setUserInfoId(userInfoId);
        entity.setLastUpdate(TypeLastUpdateEnum.CREATE_USER);
        insert(entity);
    }
}
