package com.vertex.oauth.restriction;

import com.vertex.backendcore.service.JPARestriction;
import com.vertex.backendcore.util.PredicateFactory;
import com.vertex.core.dto.UserInfoModel;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class UserInfoRestriction implements JPARestriction {

    private final UserInfoModel.Search search;

    public UserInfoRestriction(UserInfoModel.Search search) {
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
                .stringContains("firstName", search.firstName())
                .stringContains("lastName", search.lastName())
                .stringContains("nationalNo", search.nationalNo())
                .equalsValue("userInfoTypeCodeTypeItemId", search.userInfoTypeCodeTypeItemId())
                .build();
    }
}
