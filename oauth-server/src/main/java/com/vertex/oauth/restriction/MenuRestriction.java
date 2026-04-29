package com.vertex.oauth.restriction;

import com.vertex.backendcore.entity.PermissionEntity;
import com.vertex.backendcore.service.JPARestriction;
import com.vertex.backendcore.util.PredicateFactory;
import com.vertex.core.dto.MenuModel;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Objects;

public class MenuRestriction implements JPARestriction {

    private final MenuModel search;
    private final Boolean isMenuSearchKey;

    public MenuRestriction(MenuModel search, Boolean isMenuSearchKey) {
        this.search = search;
        this.isMenuSearchKey = isMenuSearchKey;
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
                .stringContains("title", search.getTitle())
                .equalsValue("active", search.getActive())
                .equalsValue("isSystemDataEntry", search.getIsSystemDataEntry())
                .applyCustomPredicateIf(!isMenuSearchKey, predicates -> {
                    List<String> authorities =
                            Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication())
                                    .getAuthorities()
                                    .stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .toList();

                    if (authorities.isEmpty()) {
                        authorities = List.of("");
                    }

                    Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
                    Root<PermissionEntity> from = subquery.from(PermissionEntity.class);
                    subquery.select(from.get("menuId"))
                            .where(criteriaBuilder.in(from.get("role")).value(authorities))
                            .distinct(true);
                    predicates.add(criteriaBuilder.in(root.get("id")).value(subquery.getSelection()));
                })
                .build();
    }
}
