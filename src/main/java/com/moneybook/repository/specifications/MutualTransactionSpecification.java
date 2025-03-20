package com.moneybook.repository.specifications;

import com.moneybook.model.MutualTransaction;
import com.moneybook.model.enums.TransactionStatus;
import com.moneybook.repository.specifications.helper.SpecificationUtil;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
public class MutualTransactionSpecification {

    // include helper methods to generate filters for the specification
    private SpecificationUtil util;

    public Specification<MutualTransaction> buildSpecification(String userID, TransactionStatus status,
                                                               Map<String, String> filters) {
        return (root, query, criteriaBuilder) -> {
            // Build predicate for borrowerID or lenderID
            Predicate predicate = criteriaBuilder.or(
                    criteriaBuilder.equal(root.get("borrowerID"), userID),
                    criteriaBuilder.equal(root.get("lenderID"), userID)
            );

            if (status != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("status"), status));
            }

            // Loop through filters and build predicates
            predicate = util.generateFilters(filters, predicate, criteriaBuilder, root);
            return predicate;
        };
    }






}
