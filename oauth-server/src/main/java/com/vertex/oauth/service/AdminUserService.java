package com.vertex.oauth.service;

import com.vertex.backendcore.entity.RoleEntity;
import com.vertex.backendcore.entity.UserEntity;
import com.vertex.backendcore.entity.UserRoleEntity;
import com.vertex.backendcore.enums.TypeLastUpdateEnum;
import com.vertex.core.config.MessageProvider;
import com.vertex.core.dto.UserDto;
import com.vertex.core.exceptions.ServiceException;
import com.vertex.core.util.PagedResponse;
import com.vertex.oauth.repository.RoleRepository;
import com.vertex.oauth.repository.UserRepository;
import com.vertex.oauth.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public PagedResponse<UserDto.Response> getList(UserDto.Search search, Pageable pageable) {
        Page<UserEntity> page;
        String query = search != null ? search.query() : null;
        if (StringUtils.isNotBlank(query)) {
            page = userRepository.findAllByUsernameContainingIgnoreCase(query.trim(), pageable);
        } else {
            page = userRepository.findAll(pageable);
        }

        List<UserDto.Response> responses = page.getContent().stream()
                .map(this::toResponse)
                .toList();

        return PagedResponse.ok(responses, page.getTotalElements(), page.hasNext());
    }

    public UserDto.Response findById(Long id) {
        return toResponse(getEntity(id));
    }

    public List<UserDto.RoleOption> getRoleOptions() {
        return roleRepository.findAllByOrderByNameAsc().stream()
                .filter(RoleEntity::isActive)
                .map(role -> new UserDto.RoleOption(role.getId(), role.getName(), role.getAliasName()))
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public UserDto.Response create(UserDto.AdminRequest request) {
        if (request == null || StringUtils.isBlank(request.username())) {
            throw new ServiceException(MessageProvider.getMessageByKey("UsernameRequired"));
        }
        if (StringUtils.isBlank(request.password())) {
            throw new ServiceException(MessageProvider.getMessageByKey("PasswordRequired"));
        }

        String username = request.username().trim();
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new ServiceException(MessageProvider.getMessageByKey("UserAlreadyExists"));
        }

        UserEntity entity = new UserEntity();
        entity.setUsername(username);
        entity.setPassword(passwordEncoder.encode(request.password()));
        entity.setEnabled(request.enabled());
        entity.setLoginAttemptNumber(0);
        entity.setLastUpdate(TypeLastUpdateEnum.CREATE_USER);
        userRepository.save(entity);

        replaceRoles(entity.getId(), request.roleIds());

        log.info("User {} created with ID {}", username, entity.getId());
        return toResponse(userRepository.findById(entity.getId()).orElseThrow());
    }

    @Transactional(rollbackFor = Exception.class)
    public UserDto.Response update(Long id, UserDto.AdminRequest request) {
        UserEntity entity = getEntity(id);

        if (request == null || StringUtils.isBlank(request.username())) {
            throw new ServiceException(MessageProvider.getMessageByKey("UsernameRequired"));
        }

        String username = request.username().trim();
        if (userRepository.existsByUsernameIgnoreCaseAndIdNot(username, id)) {
            throw new ServiceException(MessageProvider.getMessageByKey("UserAlreadyExists"));
        }

        entity.setUsername(username);
        entity.setEnabled(request.enabled());

        if (StringUtils.isNotBlank(request.password())) {
            entity.setPassword(passwordEncoder.encode(request.password()));
        }

        entity.setLastUpdate(TypeLastUpdateEnum.CREATE_USER);
        userRepository.save(entity);

        replaceRoles(entity.getId(), request.roleIds());

        log.info("User ID {} updated successfully", id);
        return toResponse(userRepository.findById(id).orElseThrow());
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        UserEntity entity = getEntity(id);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && entity.getUsername().equalsIgnoreCase(auth.getName())) {
            throw new ServiceException(MessageProvider.getMessageByKey("CannotDeleteOwnAccount"));
        }

        userRoleRepository.deleteAllByUserId(id);
        userRepository.delete(entity);
        log.info("User ID {} deleted successfully", id);
    }

    private void replaceRoles(Long userId, List<Long> requestedRoleIds) {
        userRoleRepository.deleteAllByUserId(userId);

        if (requestedRoleIds == null || requestedRoleIds.isEmpty()) {
            return;
        }

        Set<Long> roleIds = new LinkedHashSet<>(requestedRoleIds);
        List<RoleEntity> roles = roleRepository.findAllById(roleIds);

        List<UserRoleEntity> links = roles.stream().map(role -> {
            UserRoleEntity link = new UserRoleEntity();
            link.setUserId(userId);
            link.setRoleId(role.getId());
            return link;
        }).toList();

        userRoleRepository.saveAll(links);
    }

    private UserEntity getEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ServiceException(MessageProvider.getMessageByKey("UserNotFound")));
    }

    private UserDto.Response toResponse(UserEntity entity) {
        List<Long> roleIds = userRoleRepository.findAllByUserId(entity.getId()).stream()
                .map(UserRoleEntity::getRoleId)
                .toList();

        List<RoleEntity> roles = roleRepository.findAllById(roleIds).stream()
                .sorted(Comparator.comparing(RoleEntity::getName))
                .toList();

        return new UserDto.Response(
                entity.getId(),
                entity.getUsername(),
                entity.isEnabled(),
                entity.isAccountNonLocked(),
                roles.stream().map(RoleEntity::getId).toList(),
                roles.stream().map(RoleEntity::getName).toList()
        );
    }
}
