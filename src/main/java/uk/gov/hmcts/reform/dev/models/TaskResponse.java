package uk.gov.hmcts.reform.dev.models;

import java.time.LocalDateTime;

public record TaskResponse(
    Long id,
    String title,
    String description,
    TaskStatus status,
    LocalDateTime dueDateTime,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
