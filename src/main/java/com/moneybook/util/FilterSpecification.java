package com.moneybook.util;

import jakarta.persistence.criteria.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The `FilterSpecification` class is a generic implementation of the `Specification` interface
 * used to filter and query entities based on a set of criteria.
 *
 * @param <T> the type of the entity to be filtered
 */
public class FilterSpecification<T> implements Specification<T> {
    private final Map<String, String> filters;

    /**
     * Constructs a new `FilterSpecification` with the given filters.
     *
     * @param filters a map of filter criteria where the key is the field name and the value is the filter value
     */
    public FilterSpecification(Map<String, String> filters) {
        this.filters = filters;
    }

    /**
     * Converts the filter criteria into a `Predicate` to be used in a query.
     *
     * @param root  the root type in the from clause
     * @param query the criteria query
     * @param cb    the criteria builder
     * @return a `Predicate` representing the filter criteria
     */
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

    /**
     * Adds a numeric predicate to the list of predicates based on the operator.
     *
     * @param predicates the list of predicates
     * @param cb         the criteria builder
     * @param root       the root type in the from clause
     * @param field      the field name
     * @param operator   the operator (e.g., "gte", "lte", "=")
     * @param value      the numeric value to compare
     * @param <N>        the type of the numeric value
     */
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

    /**
     * Adds a date predicate to the list of predicates based on the operator.
     *
     * @param predicates the list of predicates
     * @param cb         the criteria builder
     * @param root       the root type in the from clause
     * @param field      the field name
     * @param operator   the operator (e.g., "gte", "lte", "=")
     * @param value      the date value to compare
     */
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
