package com.vertex.core.api.oauth;

import com.vertex.core.dto.UserDto;
import com.vertex.core.util.PagedResponse;
import com.vertex.core.util.ResponseMessage;
import com.vertex.core.util.RestResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SuppressWarnings("unused")
public interface UserApi {

    String BASE_URL = "/api/user";

    @PostMapping(value = {BASE_URL + "/page"}, produces = MediaType.APPLICATION_JSON_VALUE)
    PagedResponse<UserDto.Response> getList(
            @RequestBody(required = false) UserDto.Search search,
            Pageable pageable
    );

    @GetMapping(value = {BASE_URL + "/options"}, produces = MediaType.APPLICATION_JSON_VALUE)
    RestResponse<List<UserDto.RoleOption>> getRoleOptions();

    @GetMapping(value = {BASE_URL + "/{id}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    RestResponse<UserDto.Response> findById(@PathVariable("id") Long id);

    @PostMapping(value = {BASE_URL}, produces = MediaType.APPLICATION_JSON_VALUE)
    RestResponse<UserDto.Response> create(@RequestBody UserDto.AdminRequest request);

    @PutMapping(value = {BASE_URL + "/{id}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    RestResponse<UserDto.Response> update(
            @PathVariable("id") Long id,
            @RequestBody UserDto.AdminRequest request
    );

    @DeleteMapping(value = {BASE_URL + "/{id}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseMessage delete(@PathVariable("id") Long id);
}
