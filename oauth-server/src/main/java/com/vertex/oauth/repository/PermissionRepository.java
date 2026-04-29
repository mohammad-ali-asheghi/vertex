package com.vertex.oauth.repository;

import com.vertex.backendcore.entity.PermissionEntity;
import com.vertex.backendcore.repository.AbstractDAO;
import com.vertex.core.dto.interfaces.UnrelatedMenuProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends AbstractDAO<PermissionEntity> {

    List<PermissionEntity> getByRole(String role);

    @Query(value = """
            SELECT sm.id AS id,
                   sm.title AS title,
                   sm.search_key AS searchKey
            FROM sec_menu sm
            JOIN sec_role sr ON sr.name = :role
            WHERE sm.application_id = sr.application_id
              AND NOT EXISTS (
                  SELECT 1 FROM permission p
                  WHERE p.menu_id = sm.id AND p.role = :role
              )
            """, nativeQuery = true)
    List<UnrelatedMenuProjection> getUnrelatedMenusByRole(@Param("role") String role);
}
