package com.vertex.oauth.config;

import com.vertex.backendcore.entity.*;
import com.vertex.backendcore.enums.TypeLastUpdateEnum;
import com.vertex.core.enums.PermissionEnum;
import com.vertex.oauth.repository.*;
import com.vertex.oauth.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class DataInitializer {

    private final UserService userService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final MenuRepository menuRepository;
    private final PermissionRepository permissionRepository;
    private final String username;
    private final String password;
    private final String roleName;

    public DataInitializer(
            UserService userService,
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository,
            MenuRepository menuRepository,
            PermissionRepository permissionRepository,
            @Value("${security.default.username}") String username,
            @Value("${security.default.password}") String password,
            @Value("${security.default.role-name}") String roleName
    ) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.menuRepository = menuRepository;
        this.permissionRepository = permissionRepository;
        this.username = username;
        this.password = password;
        this.roleName = roleName;
    }

    @Bean
    public Boolean init() {
        if (userService.existsByUsername(username)) {
            log.info("username exists");
            syncMissingRolePermissions();
            return false;
        }
        Long userId = createUser();
        Long roleId = createRole();
        createUserRole(userId, roleId);
        syncMissingRolePermissions();
        return true;
    }

    private void syncMissingRolePermissions() {
        int mask = PermissionEnum.calculateMask(Arrays.asList(
                PermissionEnum.READ, PermissionEnum.CREATE,
                PermissionEnum.UPDATE, PermissionEnum.DELETE
        ));

        List<MenuEntity> allMenus = menuRepository.findAll();
        List<PermissionEntity> existingPermissions = permissionRepository.getByRole(roleName);

        Set<Long> existingPermissionMenuIds = (existingPermissions == null) ? Collections.emptySet() :
                existingPermissions.stream()
                        .map(PermissionEntity::getMenuId)
                        .collect(Collectors.toSet());

        List<PermissionEntity> newEntities = allMenus.stream()
                .filter(menu -> !existingPermissionMenuIds.contains(menu.getId()))
                .map(menu -> buildDefaultPermission(mask, menu.getId()))
                .toList();

        if (!newEntities.isEmpty()) {
            permissionRepository.saveAll(newEntities);
            log.info("Added {} new permissions for role: {}", newEntities.size(), roleName);
        }
    }

    private PermissionEntity buildDefaultPermission(int mask, Long menuId) {
        PermissionEntity entity = new PermissionEntity();
        entity.setRole(roleName);
        entity.setPermission(mask);
        entity.setMenuId(menuId);
        entity.setCreatedBy(username);
        entity.setDevice(OauthConstant.DEFAULT_DEVICE);
        entity.setUserIp(OauthConstant.LOCAL_HOST);
        return entity;
    }

    private Long createUser() {
        UserEntity entity = new UserEntity();
        entity.setUsername(username);
        entity.setPassword(password);
        entity.setEnabled(true);
        entity.setLoginAttemptNumber(0);
        entity.setLastUpdate(TypeLastUpdateEnum.CREATE_USER);
        userRepository.save(entity);
        log.info("username created with id: {}", entity.getId());
        return entity.getId();
    }

    private Long createRole() {
        RoleEntity entity = new RoleEntity();
        entity.setActive(true);
        entity.setAliasName(OauthConstant.DEFAULT_ALIS_NAME);
        entity.setApplicationId(OauthConstant.OAUTH_APPLICATION_ID);
        entity.setName(roleName);
        entity.setDescription("description");
        roleRepository.save(entity);
        log.info("role created with id: {}", entity.getId());
        return entity.getId();
    }

    private void createUserRole(Long userId, Long roleId) {
        UserRoleEntity entity = new UserRoleEntity();
        entity.setUserId(userId);
        entity.setRoleId(roleId);
        userRoleRepository.save(entity);
        log.info("user-role created with id: {}", entity.getId());
    }
}
