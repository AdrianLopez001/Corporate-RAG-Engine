package com.enterprise.rag.dto;

public record IngestionResponse(
        String filename,
        int chunksCreated,
        String message
) {}
