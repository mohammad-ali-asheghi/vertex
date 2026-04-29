package com.vertex.oauth.restriction;

import com.vertex.backendcore.service.JPARestriction;
import com.vertex.backendcore.util.PredicateFactory;
import com.vertex.core.dto.ApplicationModel;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class ApplicationRestriction implements JPARestriction {

    private final ApplicationModel model;

    public ApplicationRestriction(ApplicationModel model) {
        this.model = model;
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
                .stringContains("name", model.getName())
                .stringContains("title", model.getTitle())
                .stringContains("url", model.getUrl())
                .build();
    }
}
