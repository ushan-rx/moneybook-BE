package com.moneybook.repository.custom;

import com.moneybook.dto.transaction.MutualTransactionsAllDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface MutualTransactionRepoCustom {
    Page<MutualTransactionsAllDto> findAllTransactionsWithFriendNamesAndFilters(
            String userId,
            Map<String, String> filters,
            Pageable pageable
    );
}
