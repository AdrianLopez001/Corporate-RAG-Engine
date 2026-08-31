package com.enterprise.rag.dto;

public record ChunkDetail(
        String snippet,
        String source,
        double similarityScore
) {}
