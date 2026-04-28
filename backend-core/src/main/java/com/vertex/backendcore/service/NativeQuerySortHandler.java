package com.vertex.backendcore.service;

import com.vertex.core.util.StringUtil;
import jakarta.persistence.Column;
import org.springframework.data.domain.Sort;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class NativeQuerySortHandler {
    private static final Class<?> columnClass;
    private static final Class<?> idAttributeClass;
    private static final ConcurrentHashMap<String, Map<String, String>> cache = new ConcurrentHashMap<>();

    static {
        try {
            columnClass = Class.forName("javax.persistence.Column");
            idAttributeClass = Class.forName("javax.persistence.Id");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a map where the keys are the declared fields names that are in the {@code clz}
     * and the value is the name provided in {@link Column} annotation.
     *
     * @param clz Class to get the fields from.
     * @return A map that contains declared fields names and their
     * corresponding {@link Column} name value.
     */
    private static Map<String, String> getFieldsColumnNames(Class<?> clz) {
        if (clz == null) throw new IllegalArgumentException("Class is null");

        Map<String, String> output = new HashMap<>();

        Class<?> superclass = clz.getSuperclass();
        if (superclass != null) {
            // Include fields from the superclass (e.g., ID). Given the limited inheritance,
            // this should have minimal performance impact on the initial execution.
            Map<String, String> superClassColumns = getFieldsColumnNames(superclass);
            output.putAll(superClassColumns);
        }

        for (Field field : clz.getDeclaredFields()) {
            for (Annotation annotation : field.getDeclaredAnnotations()) {
                if (annotation.annotationType() == columnClass) {
                    String name = ((Column) annotation).name();
                    if (StringUtil.isEmpty(name)) continue;

                    output.put(field.getName(), name);
                    break;
                }

                if (annotation.annotationType() == idAttributeClass) {
                    output.put(field.getName(), field.getName());
                    break;
                }
            }
        }

        return output;
    }

    /**
     * Generates a new {@link Sort} that can be used by JPA native queries.
     *
     * @param clz  Class that the sorting is going to happen on, typically is a View or Entity.
     * @param sort Sort.
     * @return An instance of {@link Sort} to be used by JPA native query.
     */
    public static Sort getNativeQuerySort(Class<?> clz, Sort sort) {
        if (clz == null) throw new IllegalArgumentException("Class must not be null");
        if (sort == null) throw new IllegalArgumentException("sort must no be null.");

        Map<String, String> columnNames = cache.computeIfAbsent(clz.getName(), k -> {
            try {
                return getFieldsColumnNames(Class.forName(k));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        List<Sort.Order> orders = new ArrayList<>();
        for (Sort.Order s : sort) {
            if (StringUtil.isEmpty(s.getProperty())) {
                continue;
            }

            String columnName = columnNames.get(s.getProperty());
            if (StringUtil.isEmpty(columnName)) throw new IllegalArgumentException("Column name must not be empty");

            orders.add(new Sort.Order(s.isAscending() ? Sort.Direction.ASC : Sort.Direction.DESC, columnName));
        }

        return Sort.by(orders);
    }
}