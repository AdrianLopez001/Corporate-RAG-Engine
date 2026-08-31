package com.enterprise.rag.controller;

import com.enterprise.rag.dto.ChatResponse;
import com.enterprise.rag.dto.QueryRequest;
import com.enterprise.rag.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/chat")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "RAG-powered chat endpoints")
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "Ask a question", description = "Retrieves relevant document chunks with similarity scores and generates an answer using LLM.")
    @PostMapping("/query")
    public ResponseEntity<ChatResponse> query(@Valid @RequestBody QueryRequest request) {
        return ResponseEntity.ok(chatService.askQuestion(request));
    }

    @Operation(summary = "Stream answer tokens (SSE)", description = "Streams LLM answer tokens in real-time via Server-Sent Events.")
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> queryStream(@Valid @RequestBody QueryRequest request) {
        return chatService.askQuestionStream(request);
    }
}
