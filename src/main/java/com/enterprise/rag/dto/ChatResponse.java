package com.enterprise.rag.dto;

import java.util.List;

public record ChatResponse(
        String answer,
        List<String> sources
) {}
