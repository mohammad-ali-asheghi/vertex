package com.vertex.oauth.controller;


import com.vertex.backendcore.entity.redis.CodeTypeItemDocument;
import com.vertex.backendcore.service.redis.CodeTypeItemService;
import com.vertex.core.api.oauth.CodeTypeItemApi;
import com.vertex.core.dto.CodeTypeItemModel;
import com.vertex.core.exceptions.ServiceException;
import com.vertex.core.util.ResponseMessage;
import com.vertex.core.util.RestResponse;
import com.vertex.oauth.mapper.CodeTypeItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CodeTypeItemController implements CodeTypeItemApi {

    private final CodeTypeItemService service;

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).CONSTANT_MENU, T(com.vertex.core.enums.PermissionEnum).CREATE)")
    @Override
    public ResponseMessage create(CodeTypeItemModel request) {
        service.saveOrUpdateData(CodeTypeItemMapper.get().toDocument(request));
        return ResponseMessage.success();
    }

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).CONSTANT_MENU, T(com.vertex.core.enums.PermissionEnum).UPDATE)")
    @Override
    public ResponseMessage update(CodeTypeItemModel request) {
        CodeTypeItemDocument document = service.getData(request.getId());
        if (document == null)
            throw new ServiceException("Cannot find any record with this id");
        document.setFarsiTitle(request.getFarsiTitle());
        document.setPriority(request.getPriority());
        document.setDescription(request.getDescription());
        document.setIsDisable(request.getIsDisable());
        service.saveOrUpdateData(document);
        return ResponseMessage.success();
    }

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).CONSTANT_MENU, T(com.vertex.core.enums.PermissionEnum).READ)")
    @Override
    public RestResponse<List<CodeTypeItemModel>> list(String codeTypeId) {
        return RestResponse.ok(CodeTypeItemMapper.get().toModel(service.searchByCodeTypeId(codeTypeId)));
    }

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).CONSTANT_MENU, T(com.vertex.core.enums.PermissionEnum).READ)")
    @Override
    public RestResponse<CodeTypeItemModel> get(String id) {
        return RestResponse.ok(CodeTypeItemMapper.get().toModel(service.getData(id)));
    }
}
