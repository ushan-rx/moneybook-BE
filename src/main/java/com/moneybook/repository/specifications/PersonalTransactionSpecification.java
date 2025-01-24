package com.moneybook.repository.specifications;

import com.moneybook.model.PersonalTransaction;
import com.moneybook.repository.specifications.util.SpecificationUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
public class PersonalTransactionSpecification {

    private SpecificationUtil util;

    public Specification<PersonalTransaction> buildSpecification(String userId, Map<String, String> filters) {
        return (root, query, criteriaBuilder) -> {
            // Build predicate for userId
            var predicate = criteriaBuilder.equal(root.get("userId"), userId);
            // Loop through filters and build predicates
            if (filters != null) {
                predicate = util.generateFilters(filters, predicate, criteriaBuilder, root);
            }
            return predicate;
        };
    }
}
