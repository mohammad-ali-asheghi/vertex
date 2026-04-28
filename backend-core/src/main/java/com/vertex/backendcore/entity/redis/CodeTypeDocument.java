package com.vertex.backendcore.entity.redis;

import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Searchable;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.redis.core.index.Indexed;

@Data
@Document("CodeType")
public class CodeTypeDocument {

    @Id
    private String id;

    @Searchable
    private String title;

    @Indexed
    private Boolean isEnum;

    @Indexed
    private String category;
}
