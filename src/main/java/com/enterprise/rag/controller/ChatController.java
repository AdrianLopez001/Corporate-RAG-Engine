package com.enterprise.rag.controller;

import com.enterprise.rag.dto.ChatResponse;
import com.enterprise.rag.dto.QueryRequest;
import com.enterprise.rag.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat")
@CrossOrigin(origins = "${cors.allowed-origins:*}")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "RAG-powered chat endpoints")
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "Ask a question", description = "Retrieves relevant document chunks and generates an answer using the configured LLM.")
    @PostMapping("/query")
    public ResponseEntity<ChatResponse> query(@Valid @RequestBody QueryRequest request) {
        return ResponseEntity.ok(chatService.askQuestion(request.query()));
    }
}
