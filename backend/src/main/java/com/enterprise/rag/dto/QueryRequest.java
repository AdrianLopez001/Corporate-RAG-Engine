package com.enterprise.rag.dto;

import jakarta.validation.constraints.NotBlank;

public record QueryRequest(
        @NotBlank(message = "Query must not be blank.")
        String query
) {}
