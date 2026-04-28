package com.vertex.backendcore.repository.redis;

import com.redis.om.spring.repository.RedisDocumentRepository;
import com.vertex.backendcore.entity.redis.CodeTypeDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CodeTypeRepository extends RedisDocumentRepository<CodeTypeDocument, String> {

    Page<CodeTypeDocument> findByTitleContaining(String title, Pageable pageable);

    Page<CodeTypeDocument> findByTitleContainingAndCategory(String title, String category, Pageable pageable);

    Page<CodeTypeDocument> findByCategory(String category, Pageable pageable);
}