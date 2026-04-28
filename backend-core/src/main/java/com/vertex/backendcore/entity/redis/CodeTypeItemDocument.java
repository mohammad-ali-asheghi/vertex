package com.vertex.backendcore.entity.redis;

import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import com.redis.om.spring.annotations.Searchable;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Document("CodeTypeItem")
public class CodeTypeItemDocument {

    @Id
    private String id;

    @Indexed
    private String codeTypeId;

    @Searchable
    private String farsiTitle;

    @Searchable
    private String englishTitle;

    @Indexed
    private Integer priority;

    private String description;

    @Indexed
    private Boolean isDisable;
}