package com.enterprise.rag.controller;

import com.enterprise.rag.dto.ChatResponse;
import com.enterprise.rag.dto.QueryRequest;
import com.enterprise.rag.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat")
@CrossOrigin(origins = "${cors.allowed-origins:*}")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/query")
    public ResponseEntity<ChatResponse> query(@Valid @RequestBody QueryRequest request) {
        ChatResponse response = chatService.askQuestion(request.query());
        return ResponseEntity.ok(response);
    }
}
