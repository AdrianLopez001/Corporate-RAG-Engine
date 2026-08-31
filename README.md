# 🔍 Corporate RAG Engine

[![CI](https://github.com/AdrianLopez001/Corporate-RAG-Engine/actions/workflows/ci.yml/badge.svg)](https://github.com/AdrianLopez001/Corporate-RAG-Engine/actions)
[![Java 21](https://img.shields.io/badge/Java_21-ED8B00?style=flat&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring AI](https://img.shields.io/badge/Spring_AI-6DB33F?style=flat&logo=spring&logoColor=white)](https://docs.spring.io/spring-ai/)
[![pgvector](https://img.shields.io/badge/pgvector-336791?style=flat&logo=postgresql&logoColor=white)](https://github.com/pgvector/pgvector)
[![React 19](https://img.shields.io/badge/React_19-61DAFB?style=flat&logo=react&logoColor=black)](https://react.dev/)
[![Vite](https://img.shields.io/badge/Vite-646CFF?style=flat&logo=vite&logoColor=white)](https://vitejs.dev/)
[![OpenAI](https://img.shields.io/badge/OpenAI_API-412991?style=flat&logo=openai&logoColor=white)](https://openai.com/)

Mecanismo corporativo de busca semântica em documentos (PDFs, relatórios, DOCX) utilizando **Retrieval-Augmented Generation (RAG)**, Spring AI, Java 21, PostgreSQL (`pgvector`), Docker e React 19 + Vite.

---

## 🏛️ Arquitetura do Sistema

O sistema é dividido em duas camadas principais de micro-serviços:

```
[ Frontend: React 19 + Vite + Tailwind ]
                │
                │ HTTP / REST
                ▼
[ Backend: Spring Boot 3.3 + Spring AI ]
       │                        │
       ▼                        ▼
[ Apache Tika Parser ]    [ OpenAI Embedding ]
       │                        │
       └────────────┬───────────┘
                    ▼
       [ PostgreSQL + pgvector ]
```

1. **Ingestão Semântica de Documentos**: O sistema faz upload de arquivos (PDF, DOCX, TXT), realiza a extração com Apache Tika, quebra o texto em chunks semânticos (`TokenTextSplitter`) e gera embeddings via `text-embedding-3-small`.
2. **Armazenamento Vetorial**: Os embeddings são indexados no PostgreSQL através da extensão `pgvector` usando índice HNSW (Hierarchical Navigable Small World).
3. **Busca Semântica e Pergunta-Resposta**: A consulta do usuário gera um embedding em tempo real, executa busca por similaridade de cosseno com limiar `0.70`, recupera o contexto relevante e consulta o GPT-4o-mini sem alucinações.

---

## 🛠️ Stack Tecnológica

| Camada | Tecnologia / Libs |
|---|---|
| **Backend** | Java 21, Spring Boot 3.3, Spring AI 1.0.0, Apache Tika, Lombok |
| **Banco Vetorial** | PostgreSQL 16 + pgvector (HNSW) |
| **Modelos AI** | OpenAI `text-embedding-3-small` & `GPT-4o-mini` |
| **Frontend** | React 19, TypeScript, Vite, Tailwind CSS v4, Lucide React, Oxlint |
| **Infraestrutura** | Docker, Docker Compose |

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
- Docker & Docker Compose
- Java 21 (para rodar o backend localmente sem Docker)
- Node.js 20+ (para rodar o frontend)
- Chave de API da OpenAI (`OPENAI_API_KEY`)

### 1. Iniciar Banco de Dados Vetorial & Backend via Docker

```bash
cp .env.example .env
# Adicione sua OPENAI_API_KEY no arquivo .env

docker compose up -d
```

O backend Spring Boot estará disponível em `http://localhost:8080` (Swagger UI em `http://localhost:8080/swagger-ui.html`).

### 2. Iniciar o Frontend em React

```bash
# Na raiz do repositório
npm install
npm run dev
```

O frontend estará disponível em `http://localhost:5173`.

---

## 🧪 Testes Automatizados

O backend possui testes unitários que cobrem a ingestão de documentos e o pipeline de chat RAG:

```bash
cd backend
./mvnw test
```

---

## 📄 Licença

MIT License.
