package com.moneybook.repository.custom;

import com.moneybook.dto.transaction.MutualTransactionsAllDto;
import com.moneybook.model.enums.TransactionStatus;
import com.moneybook.model.enums.TransactionType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class MutualTransactionRepoCustomImpl implements MutualTransactionRepoCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<MutualTransactionsAllDto> findAllTransactionsWithFriendNamesAndFilters(
            String userId,
            Map<String, String> filters,
            Pageable pageable) {

        // Build the JPQL query with dynamic WHERE conditions
        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT new com.moneybook.dto.transaction.MutualTransactionsAllDto(");
        jpql.append("mt.transactionID, mt.transactionName, mt.amount, mt.transactionDate, ");
        jpql.append("CAST(mt.transactionType AS string), mt.description, ");
        jpql.append("CASE WHEN mt.lenderID = :userId ");
        jpql.append("THEN CONCAT(borrower.firstName, ' ', borrower.lastName) ");
        jpql.append("ELSE CONCAT(lender.firstName, ' ', lender.lastName) END, ");
        jpql.append("mt.status) ");
        jpql.append("FROM MutualTransaction mt ");
        jpql.append("LEFT JOIN NormalUser borrower ON mt.borrowerID = borrower.userId ");
        jpql.append("LEFT JOIN NormalUser lender ON mt.lenderID = lender.userId ");
        jpql.append("WHERE (mt.borrowerID = :userId OR mt.lenderID = :userId)");

        // Add dynamic filter conditions
        List<String> conditions = new ArrayList<>();
        Map<String, Object> parameters = new java.util.HashMap<>();
        parameters.put("userId", userId);

        addFilterConditions(filters, conditions, parameters);

        // Append filter conditions to query
        for (String condition : conditions) {
            jpql.append(" AND ").append(condition);
        }

        jpql.append(" ORDER BY mt.transactionDate DESC");

        // Create and execute the main query
        TypedQuery<MutualTransactionsAllDto> query = entityManager.createQuery(jpql.toString(), MutualTransactionsAllDto.class);

        // Set parameters
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        // Apply pagination
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<MutualTransactionsAllDto> results = query.getResultList();

        // Get total count
        long total = getTotalCountWithFilters(userId, filters);

        return new PageImpl<>(results, pageable, total);
    }

    private void addFilterConditions(Map<String, String> filters, List<String> conditions, Map<String, Object> parameters) {
        // Status filter
        if (filters.containsKey("status") && !filters.get("status").isEmpty()) {
            try {
                TransactionStatus status = TransactionStatus.valueOf(filters.get("status").toUpperCase());
                conditions.add("mt.status = :status");
                parameters.put("status", status);
            } catch (IllegalArgumentException e) {
                // Invalid status, ignore
            }
        }

        // Transaction type filter
        if (filters.containsKey("transactionType") && !filters.get("transactionType").isEmpty()) {
            try {
                TransactionType transactionType = TransactionType.valueOf(filters.get("transactionType").toUpperCase());
                conditions.add("mt.transactionType = :transactionType");
                parameters.put("transactionType", transactionType);
            } catch (IllegalArgumentException e) {
                // Invalid transaction type, ignore
            }
        }

        // Transaction name filter (like search)
        if (filters.containsKey("transactionName") && !filters.get("transactionName").isEmpty()) {
            String transactionName = filters.get("transactionName");
            conditions.add("LOWER(mt.transactionName) LIKE LOWER(:transactionName)");
            parameters.put("transactionName", "%" + transactionName + "%");
        }

        // Date range filters
        if (filters.containsKey("transactionDate_gte") && !filters.get("transactionDate_gte").isEmpty()) {
            try {
                OffsetDateTime fromDate = OffsetDateTime.parse(filters.get("transactionDate_gte"));
                conditions.add("mt.transactionDate >= :fromDate");
                parameters.put("fromDate", fromDate);
            } catch (Exception e) {
                // Invalid date format, ignore
            }
        }

        if (filters.containsKey("transactionDate_lte") && !filters.get("transactionDate_lte").isEmpty()) {
            try {
                OffsetDateTime toDate = OffsetDateTime.parse(filters.get("transactionDate_lte"));
                conditions.add("mt.transactionDate <= :toDate");
                parameters.put("toDate", toDate);
            } catch (Exception e) {
                // Invalid date format, ignore
            }
        }

        // Amount range filters
        if (filters.containsKey("amount_gte") && !filters.get("amount_gte").isEmpty()) {
            try {
                BigDecimal minAmount = new BigDecimal(filters.get("amount_gte"));
                conditions.add("mt.amount >= :minAmount");
                parameters.put("minAmount", minAmount);
            } catch (NumberFormatException e) {
                // Invalid amount format, ignore
            }
        }

        if (filters.containsKey("amount_lte") && !filters.get("amount_lte").isEmpty()) {
            try {
                BigDecimal maxAmount = new BigDecimal(filters.get("amount_lte"));
                conditions.add("mt.amount <= :maxAmount");
                parameters.put("maxAmount", maxAmount);
            } catch (NumberFormatException e) {
                // Invalid amount format, ignore
            }
        }
    }

    private long getTotalCountWithFilters(String userId, Map<String, String> filters) {
        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT COUNT(mt) FROM MutualTransaction mt ");
        jpql.append("WHERE (mt.borrowerID = :userId OR mt.lenderID = :userId)");

        List<String> conditions = new ArrayList<>();
        Map<String, Object> parameters = new java.util.HashMap<>();
        parameters.put("userId", userId);

        addFilterConditions(filters, conditions, parameters);

        // Append filter conditions to query
        for (String condition : conditions) {
            jpql.append(" AND ").append(condition);
        }

        TypedQuery<Long> query = entityManager.createQuery(jpql.toString(), Long.class);

        // Set parameters
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        return query.getSingleResult();
    }
}
