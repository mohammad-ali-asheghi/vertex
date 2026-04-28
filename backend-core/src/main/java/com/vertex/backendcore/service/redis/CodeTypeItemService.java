package com.vertex.backendcore.service.redis;

import com.vertex.backendcore.entity.redis.CodeTypeItemDocument;
import com.vertex.backendcore.repository.redis.CodeTypeItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class CodeTypeItemService {

    private final CodeTypeItemRepository repository;

    public void saveOrUpdateData(CodeTypeItemDocument entity) {
        repository.save(entity);
    }

    public void deleteData(String id) {
        repository.deleteById(id);
    }

    public CodeTypeItemDocument getData(String id) {
        return repository.findById(id).orElseThrow();
    }

    public List<CodeTypeItemDocument> searchByFarsiTitleContaining(String query) {
        return repository.findByFarsiTitleContaining(query);
    }

    public List<CodeTypeItemDocument> searchByCodeTypeId(String typeId) {
        return repository.findByCodeTypeId(typeId);
    }

    public CodeTypeItemDocument searchByCodeTypeIdAndEnglishTitle(String typeId, String englishTitle) {
        return repository.findByCodeTypeIdAndEnglishTitle(typeId, englishTitle);
    }

    public void createList(Iterable<CodeTypeItemDocument> data) {
        repository.saveAll(data);
    }
}