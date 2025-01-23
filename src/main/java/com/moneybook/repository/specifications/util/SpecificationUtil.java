package com.moneybook.repository.specifications.util;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Map;

@Component
public class SpecificationUtil {

    public <T> Predicate generateFilters(Map<String, String> filters, Predicate predicate, CriteriaBuilder criteriaBuilder, Root<T> root){
        // Loop through filters and build predicates
        for (Map.Entry<String, String> filter : filters.entrySet()) {
            String key = filter.getKey();
            String value = filter.getValue();

            // Handle OffsetDateTime fields (dateFrom, dateTo)
            if (key.equals("dateFrom") || key.equals("dateTo")) {
                predicate = criteriaBuilder.and(predicate,
                        buildDatePredicate(key, value, "transactionDate", criteriaBuilder, root));
            }
            // Handle other string fields
            else {
                predicate = criteriaBuilder.and(predicate,
                        buildStringPredicate(key, value, criteriaBuilder, root));
            }
        }
        return predicate;
    }

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
