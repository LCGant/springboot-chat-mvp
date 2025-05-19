# Spring Boot Chat MVP

![Java](https://img.shields.io/badge/Java-24-blue?logo=java\&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-6DB33F?logo=springboot)
![CI](https://img.shields.io/github/actions/workflow/status/LCGant/springboot-chat-mvp/ci.yml?label=build\&logo=github)

> Protótipo **bem enxuto** de chat em tempo real usando **Spring Boot 3.4.5**, **WebSocket** e um pequeno conjunto de endpoints REST. Todos os dados ficam **em memória** (implementação `ConcurrentHashMap`) – não há banco de dados nem autenticação, perfeito para estudos ou PoCs rápidas.

---

## ✨ Funcionalidades atuais

| Categoria     | Descrição                                                                                               |
| ------------- | ------------------------------------------------------------------------------------------------------- |
| WebSocket     | Endpoint `/ws/chat` com `ChatWebSocketHandler` para troca de mensagens em tempo real.                   |
| REST          | CRUD mínimo de conversas e mensagens em `/conversas` (fallback caso o WebSocket não esteja disponível). |
| Armazenamento | Implementação **in‑memory** (`InMemoryChatService`).                                                    |
| Hot reload    | Dependência **spring‑boot‑devtools** ativada por padrão (reinício a cada salvamento).                   |
| Testes        | Testes unitários (AssertJ + JUnit 5) e de camada web (MockMvc).                                         |

---

## 🏗️ Estrutura rápida

```
src/main/java/
└─ com/example/chat
   ├── ChatMvpApplication          # bootstrap Spring
   ├── config/                     # WebSocketConfig
   ├── controller/
   │    ├── ChatWebSocketController  # REST /conversas
   │    └── ChatRestController       # /health
   ├── dto/                        # ChatMessageDto, Conversation* records
   ├── model/                      # ChatMessage (domínio)
   ├── service/                    # ChatService + InMemoryChatService
   └── websocket/                  # ChatWebSocketHandler (sessions)
```

---

## ⚙️ Requisitos

| Ferramenta | Versão mínima | Observações                                                      |
| ---------- | ------------- | ---------------------------------------------------------------- |
| JDK        | **24**        | Definido em `pom.xml` (`<java.version>24</java.version>`).       |
| Maven      | **3.9+**      | Wrapper `mvnw` incluso – baixa a versão correta automaticamente. |

> Caso sua IDE ainda não reconheça Java 24, instale o [JDK EA 24](https://jdk.java.net/24/) ou ajuste `pom.xml` para **17** ou **21**.

---

## 🚀 Executando

```bash
# Clone o repositório
git clone https://github.com/LCGant/springboot-chat-mvp.git
cd springboot-chat-mvp

# Build & run (modo dev com hot‑reload)
./mvnw spring-boot:run
# ➜ aplicação disponível em http://localhost:8080
```

### Testando WebSocket

Use **wscat**, **websocat** ou DevTools › Network › Frames.

```bash
wscat -c ws://localhost:8080/ws/chat
```

Exemplo de payload:

```json
{
  "conversationId": 1,
  "from": "alice",
  "to": "bob",
  "text": "Olá!"
}
```

---

## 🔌 REST API

| Verbo | Endpoint                     | Descrição                           |
| ----- | ---------------------------- | ----------------------------------- |
| POST  | `/conversas`                 | Cria/retorna ID de conversa privada |
| GET   | `/conversas/{id}/mensagens`  | Lista histórico                     |
| POST  | `/conversas/{id}/mensagens`  | Envia mensagem (fallback) – **201** |
| GET   | `/conversas/{id}/usuarios`   | Usuários online nessa conversa      |
| GET   | `/conversas/usuarios-online` | Todos online                        |
| GET   | `/conversas/{id}/tipo`       | `private` ‑ou‑ `group`              |
| GET   | `/health`                    | Health‑check simples                |

---

## 🧪 Testes

```bash
./mvnw test   # JUnit 5 + AssertJ
```

---

## 🛣️ Roadmap (ideias futuras)

* [ ] Persistência com PostgreSQL via Spring Data JPA
* [ ] Autenticação JWT + Spring Security
* [ ] Broker STOMP externo (RabbitMQ)
* [ ] Front‑end React + SockJS de exemplo
* [ ] Docker Compose para app + DB

Contribuições são muito bem‑vindas! Abra uma **issue** ou **pull request**.

---

## 📄 Licença

Liberado sob **MIT**. Veja `LICENSE`.

<p align="center">Feito com ☕ por <a href="https://github.com/LCGant">Lucas G. Antonio</a></p>
