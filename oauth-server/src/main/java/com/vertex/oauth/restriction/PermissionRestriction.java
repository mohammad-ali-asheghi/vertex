package com.vertex.oauth.restriction;

import com.vertex.backendcore.service.JPARestriction;
import com.vertex.backendcore.util.PredicateFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class PermissionRestriction implements JPARestriction {

    private final String role;

    public PermissionRestriction(String role) {
        this.role = role;
    }

    @Override
    public Specification<?> countSpec(CriteriaBuilder criteriaBuilder, CriteriaQuery criteriaQuery, Root root) {
        return this::applyFilter;
    }

    @Override
    public Specification<?> listSpec(CriteriaBuilder criteriaBuilder, CriteriaQuery criteriaQuery, Root root) {
        return this::applyFilter;
    }

    private Predicate applyFilter(Root<?> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        return PredicateFactory.initializeBuilder(root, criteriaBuilder)
                .equalsValue("role", role)
                .build();
    }
}
