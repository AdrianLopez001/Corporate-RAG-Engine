# Corporate RAG Engine

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-brightgreen)
![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0.0-blue)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-pgvector-336791)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED)
![CI](https://github.com/AdrianLopez001/Corporate-RAG-Engine/actions/workflows/ci.yml/badge.svg)

## Overview

Enterprise-grade **Retrieval-Augmented Generation (RAG)** backend built with the industry-standard Java AI stack.

The system accepts document uploads (PDF, DOCX, TXT), splits them into semantic chunks, stores embeddings in a vector database, and answers natural-language questions using **only** the retrieved context — preventing hallucinations.

## Architecture

```
[Client] → POST /api/v1/documents/ingest (PDF / DOCX / TXT)
               ↓
         [IngestionService]
         Apache Tika → TokenTextSplitter (1200 tokens, 350 overlap)
               ↓
         Embedding Model (text-embedding-3-small)
               ↓
         [pgvector — PostgreSQL / HNSW index]

[Client] → POST /api/v1/chat/query
               ↓
         [ChatService]
         similaritySearch (cosine · threshold=0.70 · topK=4)
               ↓
         System Prompt + Context → GPT-4o-mini
               ↓
         [ChatResponse: answer + source list]
```

## Tech Stack

| Layer          | Technology                        |
|----------------|-----------------------------------|
| Framework      | Spring Boot 3.3 + Spring AI 1.0.0 |
| LLM            | OpenAI GPT-4o-mini                |
| Embeddings     | text-embedding-3-small (1536 dim) |
| Vector Store   | PostgreSQL + pgvector (HNSW)      |
| Document Parser| Apache Tika                       |
| Containers     | Docker + Docker Compose           |
| Java           | 21 (Records, Text Blocks)         |
| API Docs       | SpringDoc OpenAPI 3 (Swagger UI)  |

## Running Locally

### Prerequisites
- Docker Desktop running
- OpenAI API key

### 1. Copy environment file
```bash
cp .env.example .env
# then edit .env and fill in OPENAI_API_KEY
```

### 2. Start the database
```bash
docker compose up postgres-vector -d
```

### 3. Run the application
```bash
./mvnw spring-boot:run
```

### Or run everything with Docker
```bash
docker compose up --build
```

### API Documentation
Once running, open: `http://localhost:8080/swagger-ui.html`

## API Reference

### Ingest Document
```http
POST /api/v1/documents/ingest
Content-Type: multipart/form-data

file=@report.pdf
```
```json
{
  "filename": "report.pdf",
  "chunksCreated": 12,
  "message": "Document ingested successfully."
}
```

### Ask a Question
```http
POST /api/v1/chat/query
Content-Type: application/json

{ "query": "What is the company vacation policy?" }
```
```json
{
  "answer": "According to the HR handbook, employees are entitled to...",
  "sources": ["hr-handbook.pdf"]
}
```

### Health Check
```http
GET /actuator/health
```

## Design Decisions

- **`similarityThreshold(0.70)`** — only chunks with meaningful semantic overlap reach the LLM, cutting noise and reducing hallucination risk.
- **`ProblemDetail` (RFC 7807)** — structured error responses compatible with any HTTP client.
- **Constructor injection + `@RequiredArgsConstructor`** — immutability and straightforward unit testing.
- **Source metadata per chunk** — every response includes which documents were consulted.
- **Multi-stage Dockerfile** — production image is JRE-only Alpine (~80 MB vs ~400 MB JDK).
- **Spring profiles** (`local` / `prod`) — `SimpleVectorStore` in-memory for local dev, pgvector for production.
