package com.moneybook.dto.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pagination {
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean isLast;
    private boolean isFirst;
}
