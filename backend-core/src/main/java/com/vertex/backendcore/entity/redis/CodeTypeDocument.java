package com.vertex.backendcore.entity.redis;

import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import com.redis.om.spring.annotations.Searchable;
import lombok.Data;
import org.springframework.data.annotation.Id;

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
