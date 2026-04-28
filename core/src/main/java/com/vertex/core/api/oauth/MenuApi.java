package com.vertex.core.api.oauth;

import com.vertex.core.dto.MenuModel;
import com.vertex.core.dto.view.ViewMenuDto;
import com.vertex.core.util.PagedResponse;
import com.vertex.core.util.ResponseMessage;
import com.vertex.core.util.RestResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@SuppressWarnings("unused")
public interface MenuApi {

    String BASE_URL = "/api/menu";

    @PostMapping(value = {BASE_URL + "/update"}, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseMessage update(@RequestBody MenuModel request);

    @PostMapping(value = {BASE_URL + "/get/{id}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    RestResponse<MenuModel> get(@PathVariable Long id);

    @PostMapping(value = {BASE_URL + "/search-key-list"}, produces = MediaType.APPLICATION_JSON_VALUE)
    PagedResponse<ViewMenuDto> getMenuSearchKeyList(@RequestBody MenuModel search, Pageable pageable);

    @PostMapping(value = {BASE_URL + "/sidebar-list"}, produces = MediaType.APPLICATION_JSON_VALUE)
    PagedResponse<ViewMenuDto> getSidebarList(@RequestBody MenuModel search, Pageable pageable);
}