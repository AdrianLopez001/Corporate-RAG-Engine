package com.enterprise.rag.service;

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
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngestionService {

    private final VectorStore vectorStore;

    private static final int CHUNK_SIZE = 1200;
    private static final int CHUNK_OVERLAP = 350;

    public int ingestDocument(MultipartFile file) {
        log.info("Iniciando ingestão do documento: {}", file.getOriginalFilename());

        try {
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            // Tika suporta PDF, DOCX, TXT e outros formatos automaticamente
            TikaDocumentReader reader = new TikaDocumentReader(resource);
            List<Document> rawDocuments = reader.get();

            // Adiciona metadados de origem em cada chunk
            rawDocuments.forEach(doc ->
                    doc.getMetadata().putAll(Map.of(
                            "source", file.getOriginalFilename(),
                            "contentType", file.getContentType() != null ? file.getContentType() : "unknown"
                    ))
            );

            TokenTextSplitter splitter = new TokenTextSplitter(CHUNK_SIZE, CHUNK_OVERLAP, 5, 100, true);
            List<Document> chunks = splitter.apply(rawDocuments);

            vectorStore.accept(chunks);

            log.info("Documento '{}' ingerido com sucesso em {} chunks.", file.getOriginalFilename(), chunks.size());
            return chunks.size();

        } catch (IOException e) {
            throw new DocumentIngestionException("Falha ao ler o arquivo: " + file.getOriginalFilename(), e);
        } catch (Exception e) {
            throw new DocumentIngestionException("Falha ao processar o documento para o vector store.", e);
        }
    }
}
