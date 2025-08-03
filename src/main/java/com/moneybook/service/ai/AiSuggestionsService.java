package com.moneybook.service.ai;

import com.moneybook.dto.ai.AiSuggestionsResponseDto;
import com.moneybook.dto.ai.TransactionSummaryDto;
import com.moneybook.exception.ResourceNotFoundException;
import com.moneybook.repository.NormalUserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiSuggestionsService {

    private final TransactionAnalysisService transactionAnalysisService;
    private final GeminiAiService geminiAiService;
    private final NormalUserRepo normalUserRepo;

    @Cacheable(value = "aiSuggestions", key = "#userId + '_' + #daysBack", unless = "#result == null")
    public AiSuggestionsResponseDto getSmartSuggestions(String userId, Integer daysBack) throws ResourceNotFoundException {
        // Validate user exists
        normalUserRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));

        // Default to 30 days if not specified
        int analysisWindow = daysBack != null ? daysBack : 30;

        // Validate analysis window (max 365 days for performance)
        if (analysisWindow > 365) {
            analysisWindow = 365;
        }

        log.info("Generating AI suggestions for user {} with {} days of transaction history", userId, analysisWindow);

        try {
            // Step 1: Analyze user's transaction history
            TransactionSummaryDto summary = transactionAnalysisService.analyzeUserTransactions(userId, analysisWindow);

            // Step 2: Generate AI suggestions using Gemini
            AiSuggestionsResponseDto suggestions = geminiAiService.generateSmartSuggestions(summary);

            // Step 3: Set analysis date
            suggestions.setAnalysisDate(LocalDate.now().toString());

            log.info("Successfully generated {} suggestions for user {} (cached for 2 days)",
                    suggestions.getSuggestions().size(), userId);

            return suggestions;

        } catch (Exception e) {
            log.error("Error generating AI suggestions for user {}", userId, e);
            throw new RuntimeException("Failed to generate AI suggestions. Please try again later.", e);
        }
    }

    @CacheEvict(value = "aiSuggestions", key = "#userId + '_*'", allEntries = false)
    public void clearSuggestionsCache(String userId) {
        log.info("Clearing AI suggestions cache for user {} from Redis", userId);
    }

    @CacheEvict(value = "aiSuggestions", key = "#userId + '_' + #daysBack")
    public void clearSpecificSuggestionsCache(String userId, Integer daysBack) {
        int analysisWindow = daysBack != null ? daysBack : 30;
        log.info("Clearing specific AI suggestions cache for user {} with {} days analysis", userId, analysisWindow);
    }
}
