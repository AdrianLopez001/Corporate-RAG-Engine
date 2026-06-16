# RAG Enterprise Backend

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-brightgreen)
![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0.0-blue)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-pgvector-336791)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED)
![CI](https://github.com/adrianlopes/rag-enterprise-backend/actions/workflows/ci.yml/badge.svg)

## Sobre o Projeto

Backend corporativo de **Retrieval-Augmented Generation (RAG)** construído com as tecnologias padrão de mercado para sistemas de IA em Java.

O sistema permite fazer upload de documentos (PDF, DOCX, TXT) e realizar perguntas em linguagem natural sobre o conteúdo ingerido. As respostas são geradas pelo modelo da OpenAI com base **exclusivamente** no contexto recuperado do banco vetorial, eliminando alucinações.

## Arquitetura

```
[Cliente] → POST /api/v1/documents/ingest (PDF/DOCX)
               ↓
         [IngestionService]
         Tika → TokenTextSplitter → Embeddings (text-embedding-3-small)
               ↓
         [pgvector - PostgreSQL]

[Cliente] → POST /api/v1/chat/query
               ↓
         [ChatService]
         similaritySearch (cosine, threshold=0.70, topK=4)
               ↓
         System Prompt + Contexto → GPT-4o-mini
               ↓
         [ChatResponse: answer + sources]
```

## Stack Técnica

| Camada         | Tecnologia                        |
|----------------|-----------------------------------|
| Framework      | Spring Boot 3.3 + Spring AI 1.0.0 |
| LLM            | OpenAI GPT-4o-mini                |
| Embeddings     | text-embedding-3-small (1536 dim) |
| Vector Store   | PostgreSQL + pgvector (HNSW)      |
| Leitura de Doc | Apache Tika                       |
| Containers     | Docker + Docker Compose           |
| Java           | 21 (Records, Text Blocks)         |

## Como Executar Localmente

### Pré-requisitos
- Docker e Docker Compose
- Chave de API da OpenAI

### 1. Subir o banco de dados
```bash
docker-compose up postgres-vector -d
```

### 2. Configurar a chave de API
```bash
export OPENAI_API_KEY=sk-...
```

### 3. Executar a aplicação
```bash
./mvnw spring-boot:run
```

### Ou rodar tudo com Docker
```bash
OPENAI_API_KEY=sk-... docker-compose up --build
```

## Endpoints

### Ingerir Documento
```http
POST /api/v1/documents/ingest
Content-Type: multipart/form-data

file=@documento.pdf
```

### Fazer Pergunta
```http
POST /api/v1/chat/query
Content-Type: application/json

{
  "query": "Qual é a política de férias da empresa?"
}
```

### Health Check
```http
GET /actuator/health
```

## Decisões de Design

- **`withSimilarityThreshold(0.70)`** — garante que apenas chunks semanticamente relevantes cheguem ao LLM, reduzindo ruído e alucinação.
- **`ProblemDetail` (RFC 7807)** — padrão corporativo para respostas de erro estruturadas.
- **`@RequiredArgsConstructor` + injeção via construtor** — testabilidade e imutabilidade.
- **Metadados de source por chunk** — a resposta inclui quais documentos foram consultados.
- **Multi-stage Dockerfile** — imagem de produção enxuta baseada apenas no JRE.
