package com.vertex.core.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Getter
public enum PermissionEnum {
    READ(1),        // 1
    UPDATE(2),      // 1 << 1
    CREATE(4),      // 1 << 2
    DELETE(8),      // 1 << 3
    ADMIN(16),      // 1 << 4
    DOWNLOAD(32),   // 1 << 5
    SCAN(64),       // 1 << 6
    PRINT(128);     // 1 << 7

    private final int mask;

    PermissionEnum(int mask) {
        this.mask = mask;
    }

    public static List<PermissionEnum> unpackMask(int mask) {
        return Arrays.stream(values())
                .filter(p -> (mask & p.getMask()) != 0)
                .collect(Collectors.toList());
    }

    public static int calculateMask(List<PermissionEnum> permissions) {
        if (permissions == null) return 0;
        return permissions.stream()
                .mapToInt(PermissionEnum::getMask)
                .reduce(0, (a, b) -> a | b);
    }
}