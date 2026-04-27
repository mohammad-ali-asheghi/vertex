package com.vertex.core.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;

@SuppressWarnings("unused")
@Getter
@Setter
@JsonIgnoreProperties(value = {"typeName", "entityClass"})
public class PagedResponse<E> extends ResponseMessage implements ParameterizedType {

    private List<E> resultList;
    private long resultCount;
    private int totalPage;
    private Class<E> entityClass;
    private boolean hasNext;

    public PagedResponse() {
    }

    public PagedResponse(String message, int code, List<E> resultList, long resultCount) {
        super(message, code);
        this.resultList = resultList;
        this.resultCount = resultCount;
    }

    public PagedResponse(ResponseMessage msg, List<E> resultList, long resultCount) {
        super(msg.getMessage(), msg.getCode());
        this.resultList = resultList;
        this.resultCount = resultCount;
    }

    public PagedResponse(ResponseMessage msg, List<E> resultList, long resultCount, boolean hasNext) {
        super(msg.getMessage(), msg.getCode());
        this.resultList = resultList;
        this.resultCount = resultCount;
        this.hasNext = hasNext;
    }

    @SuppressWarnings("NullableProblems")
    @JsonIgnore
    @Override
    public Type[] getActualTypeArguments() {
        return new Type[]{getEntityClass()};
    }

    @SuppressWarnings("NullableProblems")
    @JsonIgnore
    @Override
    public Type getRawType() {
        return resultList.getClass();
    }

    @JsonIgnore
    @Override
    public Type getOwnerType() {
        return null;
    }

    @JsonIgnore
    public Type getType() {
        return this;
    }

    @SuppressWarnings("unchecked")
    @JsonIgnore
    public Class<E> getEntityClass() {
        if (entityClass == null) {
            Type type = getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType paramType) {
                if (paramType.getActualTypeArguments().length == 2) {
                    if (paramType.getActualTypeArguments()[1] instanceof TypeVariable) {
                        throw new IllegalArgumentException("Could not guess entity class by reflection");
                    } else {
                        entityClass = (Class<E>) paramType.getActualTypeArguments()[0];
                    }
                } else {
                    entityClass = (Class<E>) paramType.getActualTypeArguments()[0];
                }
            } else {
                throw new IllegalArgumentException("Could not guess entity class by reflection");
            }
        }
        return entityClass;
    }

    public static <T> PagedResponse<T> ok(List<T> data, long totalCount, boolean hasNext) {
        return new PagedResponse<>(ResponseMessage.success(), data, totalCount, hasNext);
    }
}