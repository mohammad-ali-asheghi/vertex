package com.vertex.oauth.repository;

import com.vertex.backendcore.entity.UserEntity;
import com.vertex.backendcore.repository.AbstractDAO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends AbstractDAO<UserEntity> {

    Optional<UserEntity> findByUsername(String email);

    Boolean existsByUsername(String username);

    Boolean existsByUsernameIgnoreCase(String username);

    Boolean existsByUsernameIgnoreCaseAndIdNot(String username, Long id);

    Page<UserEntity> findAllByUsernameContainingIgnoreCase(String username, Pageable pageable);
}
