package uk.gov.hmcts.reform.dev.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record TaskRequest(
    @NotBlank @Size(max = 160) String title,
    @Size(max = 4000) String description,
    @NotNull TaskStatus status,
    @NotNull LocalDateTime dueDateTime
) {
}
