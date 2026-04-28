package com.vertex.core.api.oauth;

import com.vertex.core.dto.RoleModel;
import com.vertex.core.util.PagedResponse;
import com.vertex.core.util.ResponseMessage;
import com.vertex.core.util.RestResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@SuppressWarnings("unused")
public interface RoleApi {

    String BASE_URL = "/api/role";

    @PostMapping(value = {BASE_URL + "/create"}, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseMessage create(@RequestBody RoleModel request);

    @PostMapping(value = {BASE_URL + "/update"}, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseMessage update(@RequestBody RoleModel request);

    @PostMapping(value = {BASE_URL + "/delete/{id}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseMessage delete(@PathVariable Long id);

    @PostMapping(value = {BASE_URL + "/get/{id}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    RestResponse<RoleModel> get(@PathVariable Long id);

    @PostMapping(value = {BASE_URL + "/list"}, produces = MediaType.APPLICATION_JSON_VALUE)
    PagedResponse<RoleModel> getList(@RequestBody RoleModel search, Pageable pageable);
}