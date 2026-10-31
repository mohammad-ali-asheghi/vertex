package com.vertex.oauth.repository;

import com.vertex.backendcore.entity.RoleEntity;
import com.vertex.backendcore.repository.AbstractDAO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends AbstractDAO<RoleEntity> {

    List<RoleEntity> findAllByOrderByNameAsc();

    List<RoleEntity> findAllByActiveTrueOrderByNameAsc();
}
