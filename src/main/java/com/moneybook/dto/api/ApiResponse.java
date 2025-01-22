package com.moneybook.dto.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private LocalDateTime timestamp;
    private int status;
    private T data; // Generic type for the response data
    private String message;
    private String error; // Optional, used only when there's an error
    private String path;
    private Pagination pagination; // Optional, used for paginated responses
}

