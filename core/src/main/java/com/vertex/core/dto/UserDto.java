package com.vertex.core.dto;

import java.io.Serializable;
import java.util.List;

public interface UserDto {

    record AdminRequest(
            String username,
            String password,
            boolean enabled,
            List<Long> roleIds
    ) implements Serializable {
    }

    record Response(
            Long id,
            String username,
            boolean enabled,
            boolean accountNonLocked,
            List<Long> roleIds,
            List<String> roles
    ) implements Serializable {
    }

    record RoleOption(
            Long id,
            String name,
            String aliasName
    ) implements Serializable {
    }

    record Search(
            String username,
            Boolean enabled,
            String query,
            Integer page,
            Integer size,
            String sortBy,
            String sortDirection
    ) implements Serializable {
    }
}
