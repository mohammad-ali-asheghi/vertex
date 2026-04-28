package com.vertex.backendcore.repository.redis;

import com.redis.om.spring.repository.RedisDocumentRepository;
import com.vertex.backendcore.entity.redis.CodeTypeItemDocument;

import java.util.List;

public interface CodeTypeItemRepository extends RedisDocumentRepository<CodeTypeItemDocument, String> {

    List<CodeTypeItemDocument> findByFarsiTitleContaining(String text);

    List<CodeTypeItemDocument> findByCodeTypeId(String typeId);

    CodeTypeItemDocument findByCodeTypeIdAndEnglishTitle(String typeId, String englishTitle);
}