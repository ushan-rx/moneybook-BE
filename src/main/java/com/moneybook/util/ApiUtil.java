package com.moneybook.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moneybook.dto.api.Pagination;
import com.moneybook.dto.transaction.MutualTransactionFilter;
import org.springframework.data.domain.Page;

import java.util.Map;

public class ApiUtil {
    public static Pagination getPagination(Page<?> page) {
        return Pagination.builder()
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .build();
    }

    public static Map<String, String> getFilters(MutualTransactionFilter filterRequest) {
        return new ObjectMapper().convertValue(filterRequest, new TypeReference<Map<String, String>>() {});
    }
}
