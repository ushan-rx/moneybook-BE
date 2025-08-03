package com.moneybook.service.transaction;

import com.moneybook.dto.transaction.*;
import com.moneybook.exception.ResourceNotFoundException;
import com.moneybook.mappers.PersonalTransactionMapper;
import com.moneybook.model.PersonalTransaction;
import com.moneybook.model.enums.TransactionCategories;
import com.moneybook.repository.NormalUserRepo;
import com.moneybook.repository.PersonalTransactionRepo;
import com.moneybook.util.FilterSpecification;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class PersonalTransactionService {

    private final PersonalTransactionRepo repo;
    private final NormalUserRepo normalUserRepo;
    private final PersonalTransactionMapper mapper;

    @Transactional
    public PersonalTransactionDto savePersonalTransaction(PersonalTransactionCreateDto personalTransactionCreateDto)
            throws ResourceNotFoundException {
        String userId = personalTransactionCreateDto.getUserId();
        normalUserRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));

        PersonalTransaction transaction = mapper
                .toPersonalTransaction(personalTransactionCreateDto);

        // generate transactionId
        transaction.setTransactionId(UUID.randomUUID());

        PersonalTransaction savedTransaction = repo.saveAndFlush(transaction);
        return mapper.fromPersonalTransaction(savedTransaction);
    }

    @Transactional
    public PersonalTransactionDto getTransactionById(UUID transactionId) throws ResourceNotFoundException {
        PersonalTransaction transaction = repo.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction with id " + transactionId + " not found"));
        return mapper.fromPersonalTransaction(transaction);
    }

    @Transactional
    public PersonalTransactionDto deleteTransaction(UUID transactionId) throws ResourceNotFoundException {
        PersonalTransaction transaction = repo.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction with id " + transactionId + " not found"));
        repo.delete(transaction);
        return mapper.fromPersonalTransaction(transaction);
    }

    @Transactional
    public PersonalTransactionDto updateTransaction(UUID transactionId, PersonalTransactionUpdateDto updateDto)
            throws ResourceNotFoundException {
        // ** check if the user is authorized to update the transaction (add later) **

        PersonalTransaction existingTransaction = repo.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction with id " + transactionId + " not found"));
        mapper.updatePersonalTransactionFromDto(updateDto, existingTransaction);
        PersonalTransaction updatedTransaction = repo.save(existingTransaction);
        return mapper.fromPersonalTransaction(updatedTransaction);
    }

    @Transactional
    public Page<PersonalTransactionDto> getAllTransactionsByUserId(
            String userId,
            Map<String, String> filters,
            Pageable pageable) throws ResourceNotFoundException {
        normalUserRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
        filters.put("userId", userId); //
        Specification<PersonalTransaction> specifications = new FilterSpecification<>(filters);

        return repo.findAll(specifications, pageable).map(mapper::fromPersonalTransaction);
    }

    public PersonalTransactionBriefDto getCategoryExpenseBrief(String dateFrom, String dateTo) {

        OffsetDateTime dateFromParsed = OffsetDateTime.parse(dateFrom);
        OffsetDateTime dateToParsed = OffsetDateTime.parse(dateTo);
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        List<CategoryExpenseBriefDTO> categoryExpenseData = repo.getPersonalExpenseSummaryByCategory(dateFromParsed, dateToParsed, userId);
        BigDecimal totalIncome = repo.getTotalIncomeForDateRange(dateFromParsed, dateToParsed, userId);
        if (categoryExpenseData.isEmpty()) {
            return new PersonalTransactionBriefDto(Collections.emptyList(), totalIncome, BigDecimal.ZERO);
        }

        // Calculate total expense amount
        BigDecimal totalExpense = new BigDecimal(0);
        for (CategoryExpenseBriefDTO data : categoryExpenseData) {
            totalExpense = totalExpense.add(data.getTotalExpense());
        }

        // Calculate 80% threshold
        BigDecimal eightyPercentThreshold = totalExpense.multiply(new BigDecimal("0.8"));

        List<CategoryExpenseBriefDTO> result = new ArrayList<>();
        BigDecimal runningTotal = new BigDecimal(0);
        BigDecimal otherTotal = new BigDecimal(0);

        // Process each category
        for (CategoryExpenseBriefDTO data : categoryExpenseData) {
            String categoryName = data.getCategory().toString();
            BigDecimal amount = data.getTotalExpense();

            runningTotal = runningTotal.add(amount);

            // If covered more than 80% of expenses, add remaining categories to "Other"
            if (runningTotal.compareTo(eightyPercentThreshold) > 0 && !result.isEmpty()) {
                otherTotal = otherTotal.add(amount);
            } else {
                // Add as individual category
                result.add(CategoryExpenseBriefDTO.builder()
                        .category(TransactionCategories.valueOf(categoryName))
                        .totalExpense(amount)
                        .build());
            }
        }

        // Add "Others" category if there are any expenses in it
        if (otherTotal.compareTo(BigDecimal.ZERO) > 0) {
            result.add(CategoryExpenseBriefDTO.builder()
                    .category(TransactionCategories.OTHERS)
                    .totalExpense(otherTotal)
                    .build());
        }

        return PersonalTransactionBriefDto.builder()
                .categoryExpenses(result)
                .totalEarnings(totalIncome)
                .totalSpends(totalExpense)
                .build();
    }

    @Transactional
    public Page<PersonalTransactionDto> getAllPersonalTransactionsByUserId(
            String userId,
            Map<String, String> filters,
            Pageable pageable) throws ResourceNotFoundException {
        // Validate user exists
        normalUserRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));

        // Add userId to filters
        filters.put("userId", userId);

        // Create specification with filters
        Specification<PersonalTransaction> specifications = new FilterSpecification<>(filters);

        // Create pageable with sorting by transaction date descending
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("transactionDate").descending()
        );

        return repo.findAll(specifications, sortedPageable)
                .map(mapper::fromPersonalTransaction);
    }
}
