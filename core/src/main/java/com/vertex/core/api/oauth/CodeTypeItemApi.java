package com.vertex.core.api.oauth;

import com.vertex.core.dto.CodeTypeItemModel;
import com.vertex.core.util.ResponseMessage;
import com.vertex.core.util.RestResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@SuppressWarnings("unused")
public interface CodeTypeItemApi {

    String BASE_URL = "/api/code-type-item";

    @PostMapping(value = {BASE_URL + "/create"}, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseMessage create(@RequestBody CodeTypeItemModel request);

    @PostMapping(value = {BASE_URL + "/update"}, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseMessage update(@RequestBody CodeTypeItemModel request);

    @PostMapping(value = {BASE_URL + "/list"}, produces = MediaType.APPLICATION_JSON_VALUE)
    RestResponse<List<CodeTypeItemModel>> list(@RequestParam("codeTypeId") String codeTypeId);

    @PostMapping(value = {BASE_URL + "/get/{id}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    RestResponse<CodeTypeItemModel> get(@PathVariable String id);
}