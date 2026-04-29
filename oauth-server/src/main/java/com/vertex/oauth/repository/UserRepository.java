package com.vertex.oauth.repository;

import com.vertex.backendcore.entity.UserEntity;
import com.vertex.backendcore.repository.AbstractDAO;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends AbstractDAO<UserEntity> {

    Optional<UserEntity> findUsersByUsername(String username);

    Boolean existsByUsername(String username);
}
