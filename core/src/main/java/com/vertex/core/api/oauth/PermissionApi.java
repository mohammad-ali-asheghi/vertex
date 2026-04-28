package com.vertex.core.api.oauth;

import com.vertex.core.dto.PermissionModel;
import com.vertex.core.dto.interfaces.UnrelatedMenuProjection;
import com.vertex.core.dto.view.ViewPermissionDto;
import com.vertex.core.util.ResponseMessage;
import com.vertex.core.util.RestResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@SuppressWarnings("unused")
public interface PermissionApi {

    String BASE_URL = "/api/permission";

    @PostMapping(value = {BASE_URL + "/sync"}, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseMessage sync(
            @RequestBody PermissionModel.Request request,
            @RequestHeader(org.springframework.http.HttpHeaders.AUTHORIZATION) String token
    );

    @PostMapping(value = {BASE_URL + "/related-list"}, produces = MediaType.APPLICATION_JSON_VALUE)
    RestResponse<List<ViewPermissionDto>> getRelatedList(@RequestBody String role);

    @PostMapping(value = {BASE_URL + "/unrelated-list"}, produces = MediaType.APPLICATION_JSON_VALUE)
    RestResponse<List<UnrelatedMenuProjection>> getUnrelatedList(@RequestParam("role") String role);
}