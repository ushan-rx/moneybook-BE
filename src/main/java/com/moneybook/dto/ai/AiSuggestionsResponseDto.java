package com.moneybook.dto.ai;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AiSuggestionsResponseDto {

    private List<AiSuggestionDto> suggestions;
    private String analysisDate;
    private String summary; // Brief overall financial health summary
}
