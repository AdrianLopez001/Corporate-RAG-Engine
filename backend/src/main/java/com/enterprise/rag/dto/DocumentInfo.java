package com.enterprise.rag.dto;

public record DocumentInfo(
        String filename,
        String contentType,
        long chunksCount
) {}
