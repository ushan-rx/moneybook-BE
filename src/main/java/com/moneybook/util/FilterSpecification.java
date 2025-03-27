package com.moneybook.util;

import jakarta.persistence.criteria.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FilterSpecification<T> implements Specification<T> {
    private final Map<String, String> filters;

    public FilterSpecification(Map<String, String> filters) {
        this.filters = filters;
    }

    @NotNull
    @Override
    public Predicate toPredicate(@NotNull Root<T> root, @NotNull CriteriaQuery<?> query, @NotNull CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        for (Map.Entry<String, String> entry : filters.entrySet()) {
            String key = entry.getKey();
            String field = key.replace("_lte", "").replace("_gte", ""); // Extract field name
            String operator = key.endsWith("_lte") ? "lte" : key.endsWith("_gte") ? "gte" : "=";

            if (root.getModel().getAttributes().stream().noneMatch(a -> a.getName().equals(field))) {
                continue;
            }

            Class<?> fieldType = root.get(field).getJavaType();
            String value = entry.getValue();

            if (fieldType == Integer.class || fieldType == Long.class) {
                Integer intValue = Integer.parseInt(value);
                addNumberPredicate(predicates, cb, root, field, operator, intValue);
            } else if (fieldType == Double.class || fieldType == BigDecimal.class) {
                BigDecimal decimalValue = new BigDecimal(value);
                addNumberPredicate(predicates, cb, root, field, operator, decimalValue);
            } else if (fieldType == OffsetDateTime.class) {
                OffsetDateTime dateValue = OffsetDateTime.parse(value);
                addDatePredicate(predicates, cb, root, field, operator, dateValue);
            } else {
                predicates.add(cb.equal(root.get(field), value));
            }
        }
        return cb.and(predicates.toArray(new Predicate[0]));
    }


    private <N extends Number & Comparable<N>> void addNumberPredicate(
            List<Predicate> predicates, CriteriaBuilder cb,
            Root<T> root, String field, String operator, N value) {
        Path<N> path = root.get(field);
        switch (operator) {
            case "gte" -> predicates.add(cb.greaterThanOrEqualTo(path, value));
            case "lte" -> predicates.add(cb.lessThanOrEqualTo(path, value));
            default -> predicates.add(cb.equal(path, value));
        }
    }

    private void addDatePredicate(
            List<Predicate> predicates, CriteriaBuilder cb,
            Root<T> root, String field, String operator, OffsetDateTime value) {
        Path<OffsetDateTime> path = root.get(field);
        switch (operator) {
            case "gte" -> predicates.add(cb.greaterThanOrEqualTo(path, value));
            case "lte" -> predicates.add(cb.lessThanOrEqualTo(path, value));
            default -> predicates.add(cb.equal(path, value));
        }
    }
}
