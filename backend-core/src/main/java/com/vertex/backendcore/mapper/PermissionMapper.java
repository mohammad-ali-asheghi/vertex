package com.vertex.backendcore.mapper;

import com.vertex.backendcore.util.ExtendedBasePermission;
import com.vertex.core.enums.PermissionEnum;
import org.springframework.security.acls.model.Permission;

public class PermissionMapper {

    public static Permission toSpringPermission(PermissionEnum pEnum) {
        return switch (pEnum) {
            case READ -> ExtendedBasePermission.READ;
            case CREATE -> ExtendedBasePermission.CREATE;
            case UPDATE -> ExtendedBasePermission.UPDATE;
            case DELETE -> ExtendedBasePermission.DELETE;
            case DOWNLOAD -> ExtendedBasePermission.DOWNLOAD;
            case SCAN -> ExtendedBasePermission.SCAN;
            case PRINT -> ExtendedBasePermission.PRINT;
            default -> null;
        };
    }
}