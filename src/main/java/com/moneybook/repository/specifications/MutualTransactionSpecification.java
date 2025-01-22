package com.moneybook.repository.specifications;

import com.moneybook.model.MutualTransaction;
import com.moneybook.model.enums.TransactionStatus;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Map;

@Component
@Slf4j
public class MutualTransactionSpecification {
    public Specification<MutualTransaction> buildSpecification(String userID, TransactionStatus status,
                                                               Map<String, String> filters) {
        return (root, query, criteriaBuilder) -> {
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
                    if (key.equals("transactionDateFrom") || key.equals("transactionDateTo")) {
                            OffsetDateTime dateValue = OffsetDateTime.parse(value);

                            if (key.equals("transactionDateFrom")) {
                                predicate = criteriaBuilder.and(predicate,
                                        criteriaBuilder.greaterThanOrEqualTo(root.get("transactionDate"), dateValue));
                            } else {
                                predicate = criteriaBuilder.and(predicate,
                                        criteriaBuilder.lessThanOrEqualTo(root.get("transactionDate"), dateValue));
                            }
                    }
                    // Handle other string fields (e.g., transactionType)
                    else {
                        predicate = criteriaBuilder.and(predicate,
                                criteriaBuilder.like(root.get(key), "%" + value + "%"));
                    }

            }

            return predicate;
        };
    }
}
