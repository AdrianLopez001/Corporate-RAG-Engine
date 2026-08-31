package com.enterprise.rag.service;

import com.enterprise.rag.dto.DocumentInfo;
import com.enterprise.rag.exception.DocumentIngestionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngestionService {

    private final VectorStore vectorStore;

    private static final int CHUNK_SIZE = 1200;
    private static final int CHUNK_OVERLAP = 350;

    private final Map<String, DocumentInfo> documentRegistry = new ConcurrentHashMap<>();

    public int ingestDocument(MultipartFile file) {
        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "document.pdf";
        log.info("Starting ingestion for document: {}", filename);

        try {
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return filename;
                }
            };

            TikaDocumentReader reader = new TikaDocumentReader(resource);
            List<Document> rawDocuments = reader.get();

            String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
            rawDocuments.forEach(doc ->
                    doc.getMetadata().putAll(Map.of(
                            "source", filename,
                            "contentType", contentType
                    ))
            );

            TokenTextSplitter splitter = new TokenTextSplitter(CHUNK_SIZE, CHUNK_OVERLAP, 5, 100, true);
            List<Document> chunks = splitter.apply(rawDocuments);

            vectorStore.accept(chunks);

            DocumentInfo info = new DocumentInfo(filename, contentType, chunks.size());
            documentRegistry.put(filename, info);

            log.info("Document '{}' ingested successfully into {} chunks.", filename, chunks.size());
            return chunks.size();

        } catch (IOException e) {
            throw new DocumentIngestionException("Failed to read file: " + filename, e);
        } catch (Exception e) {
            throw new DocumentIngestionException("Failed to process document into vector store.", e);
        }
    }

    public List<DocumentInfo> listDocuments() {
        return new ArrayList<>(documentRegistry.values());
    }

    public boolean deleteDocument(String filename) {
        log.info("Requesting deletion for document: {}", filename);
        if (documentRegistry.containsKey(filename)) {
            documentRegistry.remove(filename);
            try {
                // VectorStore delete por id / filter se disponível
                log.info("Document '{}' removed from registry.", filename);
            } catch (Exception e) {
                log.warn("Could not delete chunks from vectorStore directly for {}", filename, e);
            }
            return true;
        }
        return false;
    }
}
