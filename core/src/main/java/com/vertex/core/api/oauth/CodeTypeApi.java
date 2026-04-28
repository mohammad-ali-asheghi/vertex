package com.vertex.core.api.oauth;

import com.vertex.core.dto.CodeTypeModel;
import com.vertex.core.util.PagedResponse;
import com.vertex.core.util.ResponseMessage;
import com.vertex.core.util.RestResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@SuppressWarnings("unused")
public interface CodeTypeApi {

    String BASE_URL = "/api/code-type";

    @PostMapping(value = {BASE_URL + "/update"}, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseMessage update(@RequestBody CodeTypeModel request);

    @PostMapping(value = {BASE_URL + "/get/{id}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    RestResponse<CodeTypeModel> get(@PathVariable String id);

    @PostMapping(value = {BASE_URL + "/list"}, produces = MediaType.APPLICATION_JSON_VALUE)
    PagedResponse<CodeTypeModel> list(@RequestBody CodeTypeModel request, Pageable pageable);
}