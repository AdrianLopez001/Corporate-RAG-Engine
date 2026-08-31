package com.enterprise.rag.controller;

import com.enterprise.rag.dto.DocumentInfo;
import com.enterprise.rag.dto.IngestionResponse;
import com.enterprise.rag.service.IngestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Tag(name = "Documents", description = "Document ingestion & management endpoints")
@CrossOrigin(origins = "*")
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

    @Operation(summary = "List ingested documents", description = "Retrieves all indexed documents in vector store.")
    @GetMapping
    public ResponseEntity<List<DocumentInfo>> listDocuments() {
        return ResponseEntity.ok(ingestionService.listDocuments());
    }

    @Operation(summary = "Delete an ingested document", description = "Removes a document and its vector embeddings.")
    @DeleteMapping("/{filename}")
    public ResponseEntity<Void> deleteDocument(@PathVariable String filename) {
        boolean removed = ingestionService.deleteDocument(filename);
        if (removed) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
