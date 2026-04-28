package com.vertex.core.api.oauth;

import com.vertex.core.dto.ApplicationModel;
import com.vertex.core.util.PagedResponse;
import com.vertex.core.util.RestResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@SuppressWarnings("unused")
public interface ApplicationApi {

    String BASE_URL = "/api/application";

    @PostMapping(value = {BASE_URL + "/get/{id}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    RestResponse<ApplicationModel> get(@PathVariable Long id);

    @PostMapping(value = {BASE_URL + "/list"}, produces = MediaType.APPLICATION_JSON_VALUE)
    PagedResponse<ApplicationModel> getList(@RequestBody ApplicationModel search, Pageable pageable);
}