package com.moneybook.dto.ai;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AiSuggestionDto {

    private String type; // "SPENDING", "SAVING", "BUDGET", "DEBT_MANAGEMENT"
    private String title;
    private String description;
    private String priority; // "HIGH", "MEDIUM", "LOW"
    private String actionable; // Specific action user can take
}
