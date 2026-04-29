package com.vertex.oauth.restriction;

import com.vertex.backendcore.service.JPARestriction;
import com.vertex.backendcore.util.PredicateFactory;
import com.vertex.core.dto.RoleModel;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class RoleRestriction implements JPARestriction {

    private final RoleModel search;

    public RoleRestriction(RoleModel search) {
        this.search = search;
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
                .stringContains("name", search.getName())
                .stringContains("aliasName", search.getAliasName())
                .equalsValue("active", search.isActive())
                .equalsValue("applicationId", search.getApplicationId())
                .build();
    }
}
