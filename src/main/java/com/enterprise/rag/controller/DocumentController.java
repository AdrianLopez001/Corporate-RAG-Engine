package com.enterprise.rag.controller;

import com.enterprise.rag.dto.IngestionResponse;
import com.enterprise.rag.service.IngestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Tag(name = "Documents", description = "Document ingestion endpoints")
public class DocumentController {

    private final IngestionService ingestionService;

    @Operation(summary = "Ingest a document", description = "Uploads and processes a document (PDF, DOCX, TXT) into the vector store.")
    @PostMapping("/ingest")
    public ResponseEntity<IngestionResponse> ingest(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        int chunks = ingestionService.ingestDocument(file);

        return ResponseEntity.ok(new IngestionResponse(
                file.getOriginalFilename(),
                chunks,
                "Document ingested successfully."
        ));
    }
}
