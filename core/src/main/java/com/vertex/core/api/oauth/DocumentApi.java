package com.vertex.core.api.oauth;

import com.vertex.core.dto.DocumentDto;
import com.vertex.core.util.PagedResponse;
import com.vertex.core.util.ResponseMessage;
import com.vertex.core.util.RestResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SuppressWarnings("unused")
public interface DocumentApi {

    String BASE_URL = "/api/documents";

    @PostMapping(value = {BASE_URL + "/page"}, produces = MediaType.APPLICATION_JSON_VALUE)
    PagedResponse<DocumentDto.Response> getList(
            @RequestBody(required = false) DocumentDto.Search search,
            Pageable pageable
    );

    @GetMapping(value = {BASE_URL + "/system-docs"}, produces = MediaType.APPLICATION_JSON_VALUE)
    RestResponse<List<DocumentDto.SystemDoc>> getSystemDocs();

    @DeleteMapping(value = {BASE_URL + "/{id}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseMessage delete(@PathVariable("id") Long id);
}
