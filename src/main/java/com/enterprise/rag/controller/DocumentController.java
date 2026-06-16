package com.enterprise.rag.controller;

import com.enterprise.rag.service.IngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final IngestionService ingestionService;

    @PostMapping("/ingest")
    public ResponseEntity<Map<String, Object>> ingest(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "O arquivo enviado está vazio."));
        }

        int chunks = ingestionService.ingestDocument(file);

        return ResponseEntity.ok(Map.of(
                "message", "Documento ingerido com sucesso.",
                "filename", file.getOriginalFilename(),
                "chunksCreated", chunks
        ));
    }
}
