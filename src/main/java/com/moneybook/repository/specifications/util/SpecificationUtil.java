package com.moneybook.repository.specifications.util;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class SpecificationUtil {
    public <T> Predicate buildDatePredicate(String key, String value, String dateField, CriteriaBuilder criteriaBuilder, Root<T> root) {
        // Handle OffsetDateTime fields
        if (key.equals("dateFrom") || key.equals("dateTo")) {
            OffsetDateTime dateValue = OffsetDateTime.parse(value);
            if (key.equals("dateFrom")) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(dateField), dateValue);
            } else {
                return criteriaBuilder.lessThanOrEqualTo(root.get(dateField), dateValue);
            }
        }else {
            throw new IllegalArgumentException("Invalid date field: " + key);
        }
    }

    public <T> Predicate buildStringPredicate(String key, String value, CriteriaBuilder criteriaBuilder, Root<T> root){
        return criteriaBuilder.like(root.get(key), "%" + value + "%");

    }
}
