package com.vertex.backendcore.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;


public interface JPARestriction {

    @SuppressWarnings("rawtypes")
    Specification countSpec(CriteriaBuilder builder, CriteriaQuery criteria, Root root);

    @SuppressWarnings("rawtypes")
    Specification listSpec(CriteriaBuilder builder, CriteriaQuery criteria, Root root);

    default boolean distinct() {
        return false;
    }
}
