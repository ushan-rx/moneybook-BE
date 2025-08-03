package com.moneybook.controller;

import com.moneybook.dto.ai.AiSuggestionsResponseDto;
import com.moneybook.dto.api.ApiResponse;
import com.moneybook.exception.ResourceNotFoundException;
import com.moneybook.service.ai.AiSuggestionsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("${api.base-path}/ai")
@RequiredArgsConstructor
@Tag(name = "AI Suggestions", description = "Smart financial suggestions powered by AI")
public class AiSuggestionsController {

    private final AiSuggestionsService aiSuggestionsService;

    @GetMapping("/suggestions/{userId}")
    @Operation(summary = "Get AI-powered financial suggestions",
               description = "Analyzes user's transaction history and provides personalized financial advice using AI")
    public ResponseEntity<ApiResponse<AiSuggestionsResponseDto>> getSmartSuggestions(
            @Parameter(description = "User ID for whom to generate suggestions")
            @PathVariable String userId,
            @Parameter(description = "Number of days to analyze (default: 30, max: 365)")
            @RequestParam(defaultValue = "30") Integer daysBack) throws ResourceNotFoundException {

        AiSuggestionsResponseDto suggestions = aiSuggestionsService.getSmartSuggestions(userId, daysBack);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<AiSuggestionsResponseDto>builder()
                        .status(HttpStatus.OK.value())
                        .timestamp(LocalDateTime.now())
                        .message("AI suggestions generated successfully")
                        .data(suggestions)
                        .build()
        );
    }

    @PostMapping("/suggestions/{userId}/refresh")
    @Operation(summary = "Refresh AI suggestions cache",
               description = "Clears cached suggestions and forces generation of new ones")
    public ResponseEntity<ApiResponse<String>> refreshSuggestions(
            @Parameter(description = "User ID for whom to refresh suggestions")
            @PathVariable String userId) {

        aiSuggestionsService.clearSuggestionsCache(userId);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<String>builder()
                        .status(HttpStatus.OK.value())
                        .timestamp(LocalDateTime.now())
                        .message("AI suggestions cache cleared successfully")
                        .data("Cache refreshed. Next request will generate new suggestions.")
                        .build()
        );
    }
}
