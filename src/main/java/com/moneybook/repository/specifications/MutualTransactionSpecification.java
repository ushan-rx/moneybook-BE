package com.moneybook.repository.specifications;

import com.moneybook.model.MutualTransaction;
import com.moneybook.model.enums.TransactionStatus;
import com.moneybook.repository.specifications.util.SpecificationUtil;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@AllArgsConstructor
public class MutualTransactionSpecification {

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
            for (Map.Entry<String, String> filter : filters.entrySet()) {
                String key = filter.getKey();
                String value = filter.getValue();

                // Handle OffsetDateTime fields (transactionDateFrom, transactionDateTo)
                if (key.equals("dateFrom") || key.equals("dateTo")) {
                    predicate = criteriaBuilder.and(predicate,
                            util.buildDatePredicate(key, value, "transactionDate", criteriaBuilder, root));
                }
                // Handle other string fields (e.g., transactionType)
                else {
                    predicate = criteriaBuilder.and(predicate,
                            util.buildStringPredicate(key, value, criteriaBuilder, root));
                }
            }
            return predicate;
        };
    }






}
