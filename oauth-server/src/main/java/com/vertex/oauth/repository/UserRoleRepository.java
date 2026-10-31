package com.vertex.oauth.repository;

import com.vertex.backendcore.entity.UserRoleEntity;
import com.vertex.backendcore.repository.AbstractDAO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends AbstractDAO<UserRoleEntity> {

    List<UserRoleEntity> findAllByUserId(Long userId);

    void deleteAllByUserId(Long userId);
}
