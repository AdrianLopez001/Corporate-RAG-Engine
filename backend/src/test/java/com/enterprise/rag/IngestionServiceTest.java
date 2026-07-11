package com.enterprise.rag;

import com.enterprise.rag.exception.DocumentIngestionException;
import com.enterprise.rag.service.IngestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class IngestionServiceTest {

    @Mock private VectorStore vectorStore;

    private IngestionService ingestionService;

    @BeforeEach
    void setUp() {
        ingestionService = new IngestionService(vectorStore);
    }

    @Test
    void shouldCallVectorStoreAfterIngestion() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain",
                "This is a sample document for testing the ingestion pipeline.".getBytes()
        );

        ingestionService.ingestDocument(file);

        verify(vectorStore).accept(anyList());
    }

    @Test
    void shouldThrowDocumentIngestionExceptionOnEmptyBytes() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "empty.pdf", "application/pdf", new byte[0]
        );

        assertThatThrownBy(() -> ingestionService.ingestDocument(emptyFile))
                .isInstanceOf(DocumentIngestionException.class);
    }
}
