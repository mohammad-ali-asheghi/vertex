package com.vertex.backendcore.service.redis;

import com.vertex.backendcore.entity.redis.CodeTypeDocument;
import com.vertex.backendcore.repository.redis.CodeTypeRepository;
import com.vertex.core.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class CodeTypeService {

    private final CodeTypeRepository repository;

    public void saveOrUpdateData(CodeTypeDocument entity) {
        repository.save(entity);
    }

    public void deleteData(String id) {
        repository.deleteById(id);
    }

    public Page<CodeTypeDocument> searchList(String title, String category, PageRequest pageRequest) {
        if (StringUtil.isEmpty(title) && StringUtil.isEmpty(category))
            return repository.findAll(pageRequest);
        else if (StringUtil.isNotEmpty(title) && StringUtil.isNotEmpty(category))
            return repository.findByTitleContainingAndCategory(title, category, pageRequest);
        else if (StringUtil.isNotEmpty(title) && StringUtil.isEmpty(category))
            return repository.findByTitleContaining(title, pageRequest);
        else
            return repository.findByCategory(category, pageRequest);
    }

    public CodeTypeDocument getData(String id) {
        return repository.findById(id).orElseThrow();
    }

    public void createList(Iterable<CodeTypeDocument> data) {
        repository.saveAll(data);
    }
}