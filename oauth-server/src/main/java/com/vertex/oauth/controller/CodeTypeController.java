package com.vertex.oauth.controller;


import com.vertex.backendcore.entity.redis.CodeTypeDocument;
import com.vertex.backendcore.service.redis.CodeTypeService;
import com.vertex.core.api.oauth.CodeTypeApi;
import com.vertex.core.config.CommonConstant;
import com.vertex.core.dto.CodeTypeModel;
import com.vertex.core.util.PagedResponse;
import com.vertex.core.util.ResponseMessage;
import com.vertex.core.util.RestResponse;
import com.vertex.oauth.mapper.CodeTypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CodeTypeController implements CodeTypeApi {

    private final CodeTypeService service;

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).CONSTANT_MENU, T(com.vertex.core.enums.PermissionEnum).UPDATE)")
    @Override
    public ResponseMessage update(CodeTypeModel request) {
        if (request.getId() == null)
            return ResponseMessage.fail("ID must not be null");
        CodeTypeDocument document = service.getData(request.getId());
        if (document == null)
            return ResponseMessage.fail("Cannot find any record with this ID");
        document.setCategory(request.getCategory());
        document.setTitle(request.getTitle());
        service.saveOrUpdateData(document);
        return ResponseMessage.success();
    }

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).CONSTANT_MENU, T(com.vertex.core.enums.PermissionEnum).READ)")
    @Override
    public RestResponse<CodeTypeModel> get(String id) {
        return RestResponse.ok(CodeTypeMapper.get().toModel(service.getData(id)));
    }

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).CONSTANT_MENU, T(com.vertex.core.enums.PermissionEnum).READ)")
    @Override
    public PagedResponse<CodeTypeModel> list(
            CodeTypeModel request,
            @PageableDefault(
                    size = CommonConstant.PAGE_SIZE,
                    sort = "id", direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        Page<CodeTypeDocument> documents = service.searchList(
                request.getTitle(),
                request.getCategory(),
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize())
        );
        return PagedResponse.ok(
                CodeTypeMapper.get().toModel(documents.getContent()),
                documents.getTotalElements(),
                documents.hasNext()
        );
    }
}
