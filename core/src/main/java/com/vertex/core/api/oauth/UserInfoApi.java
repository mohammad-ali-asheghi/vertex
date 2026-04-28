package com.vertex.core.api.oauth;

import com.vertex.core.dto.UserInfoModel;
import com.vertex.core.util.PagedResponse;
import com.vertex.core.util.ResponseMessage;
import com.vertex.core.util.RestResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@SuppressWarnings("unused")
public interface UserInfoApi {

    String BASE_URL = "/api/user-info";

    @PostMapping(value = {BASE_URL + "/create"}, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseMessage create(@RequestBody UserInfoModel.Create request);

    @PostMapping(value = {BASE_URL + "/update"}, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseMessage update(@RequestBody UserInfoModel.Update request);

    @PostMapping(value = {BASE_URL + "/delete/{id}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseMessage delete(@PathVariable Long id);

    @PostMapping(value = {BASE_URL + "/get/{id}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    RestResponse<UserInfoModel.Response> get(@PathVariable Long id);

    //todo used view
    @PostMapping(value = {BASE_URL + "/list"}, produces = MediaType.APPLICATION_JSON_VALUE)
    PagedResponse<UserInfoModel.Response> getList(@RequestBody UserInfoModel.Search search, Pageable pageable);
}