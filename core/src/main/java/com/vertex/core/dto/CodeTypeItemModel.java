package com.vertex.core.dto;


import lombok.Data;

@SuppressWarnings("unused")
@Data
public class CodeTypeItemModel {

    private String id;
    private String codeTypeId;
    private String farsiTitle;
    private String englishTitle;
    private Integer priority;
    private String description;
    private Boolean isDisable;
}
