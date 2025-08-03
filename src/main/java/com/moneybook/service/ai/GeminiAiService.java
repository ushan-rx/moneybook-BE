package com.moneybook.service.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moneybook.dto.ai.AiSuggestionDto;
import com.moneybook.dto.ai.AiSuggestionsResponseDto;
import com.moneybook.dto.ai.TransactionSummaryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiAiService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AiSuggestionsResponseDto generateSmartSuggestions(TransactionSummaryDto summary) {
        try {
            String prompt = buildAnalysisPrompt(summary);
            String geminiResponse = callGeminiApi(prompt);
            return parseGeminiResponse(geminiResponse);
        } catch (Exception e) {
            log.error("Error generating AI suggestions", e);
            return createFallbackSuggestions(summary);
        }
    }

    private String buildAnalysisPrompt(TransactionSummaryDto summary) {
        return String.format("""
            You are a personal finance advisor AI. Analyze the following financial data and provide exactly 3-4 concise, actionable suggestions.
            
            Financial Summary (Last 30 days):
            - Total Income: $%s
            - Total Expenses: $%s
            - Net Savings: $%s
            - Expenses by Category: %s
            - Total Lent to Friends: $%s
            - Total Borrowed from Friends: $%s
            - Net Mutual Balance: $%s
            - Pending Transactions: %d
            - Total Transaction Count: %d
            
            Please respond in this exact JSON format:
            {
              "suggestions": [
                {
                  "type": "SPENDING|SAVING|BUDGET|DEBT_MANAGEMENT",
                  "title": "Brief title (max 50 chars)",
                  "description": "Detailed explanation (max 150 chars)",
                  "priority": "HIGH|MEDIUM|LOW",
                  "actionable": "Specific action to take (max 100 chars)"
                }
              ],
              "summary": "Overall financial health summary (max 200 chars)"
            }
            
            Focus on:
            1. Spending patterns and potential savings
            2. Mutual transaction management
            3. Budget optimization
            4. Debt/lending balance
            
            Keep suggestions practical, specific, and achievable.
            """,
            summary.getTotalIncome(),
            summary.getTotalExpense(),
            summary.getNetSavings(),
            summary.getExpenseByCategory(),
            summary.getTotalLent(),
            summary.getTotalBorrowed(),
            summary.getNetMutualBalance(),
            summary.getPendingTransactions(),
            summary.getTotalTransactionCount()
        );
    }

    private String callGeminiApi(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", geminiApiKey);

        Map<String, Object> requestBody = Map.of(
            "contents", List.of(
                Map.of("parts", List.of(
                    Map.of("text", prompt)
                ))
            ),
            "generationConfig", Map.of(
                "temperature", 0.3,
                "maxOutputTokens", 1000
            )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(geminiApiUrl, request, String.class);

        return response.getBody();
    }

    private AiSuggestionsResponseDto parseGeminiResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            String textContent = root.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            // Extract JSON from the text content
            int jsonStart = textContent.indexOf("{");
            int jsonEnd = textContent.lastIndexOf("}") + 1;
            String jsonContent = textContent.substring(jsonStart, jsonEnd);

            return objectMapper.readValue(jsonContent, AiSuggestionsResponseDto.class);
        } catch (Exception e) {
            log.error("Error parsing Gemini response", e);
            throw new RuntimeException("Failed to parse AI response", e);
        }
    }

    private AiSuggestionsResponseDto createFallbackSuggestions(TransactionSummaryDto summary) {
        List<AiSuggestionDto> fallbackSuggestions = new ArrayList<>();

        // Basic fallback suggestions based on simple rules
        if (summary.getNetSavings().compareTo(summary.getTotalIncome().multiply(java.math.BigDecimal.valueOf(0.1))) < 0) {
            fallbackSuggestions.add(AiSuggestionDto.builder()
                    .type("SAVING")
                    .title("Increase Your Savings Rate")
                    .description("Your current savings rate is low. Try to save at least 10-20% of your income.")
                    .priority("HIGH")
                    .actionable("Set up automatic savings transfer of $" + summary.getTotalIncome().multiply(java.math.BigDecimal.valueOf(0.1)))
                    .build());
        }

        if (summary.getPendingTransactions() > 3) {
            fallbackSuggestions.add(AiSuggestionDto.builder()
                    .type("DEBT_MANAGEMENT")
                    .title("Manage Pending Transactions")
                    .description("You have multiple pending mutual transactions that need attention.")
                    .priority("MEDIUM")
                    .actionable("Follow up on " + summary.getPendingTransactions() + " pending transactions")
                    .build());
        }

        return AiSuggestionsResponseDto.builder()
                .suggestions(fallbackSuggestions)
                .summary("Analysis temporarily unavailable. Here are some basic suggestions based on your data.")
                .analysisDate(LocalDate.now().toString())
                .build();
    }
}
