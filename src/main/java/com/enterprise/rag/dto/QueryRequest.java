package com.enterprise.rag.dto;

import jakarta.validation.constraints.NotBlank;

public record QueryRequest(
        @NotBlank(message = "A query não pode ser vazia.")
        String query
) {}
