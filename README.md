# Api Spring boot com AISantander 2026AI Java Back end
# Budgeting API

API para gerenciamento de transações financeiras com integração à OpenAI para chat, transcrição de áudio e conversão de texto em voz.

## URL base

```text
http://localhost:8080
```

## Requisitos

* Java e Spring Boot
* MySQL
* Chave de acesso à OpenAI
* Variável de ambiente:

```env
OPEN_API_KEY=sua-chave
```

> Os endpoints que utilizam transcrição, chat ou geração de áudio dependem da configuração da chave da OpenAI.

## Convenções da API

### Formatos utilizados

| Tipo de operação   | Content-Type          |
| ------------------ | --------------------- |
| Requisições comuns | `application/json`    |
| Upload de áudio    | `multipart/form-data` |
| Respostas em áudio | `audio/mp3`           |
| Respostas textuais | `text/plain`          |

### Categorias disponíveis

| Categoria   | Descrição sugerida     |
| ----------- | ---------------------- |
| `GROCERIES` | Mercado e alimentação  |
| `FARM`      | Farmácia e saúde       |
| `AUTO`      | Automóvel e transporte |

### Valores monetários

Os valores devem ser enviados em **centavos**.

| Valor em reais | Valor enviado |
| -------------: | ------------: |
|       R$ 10,00 |        `1000` |
|       R$ 25,99 |        `2599` |
|       R$ 90,00 |        `9000` |

Exemplo:

```json
{
  "amount": 2599
}
```

Esse valor representa **R$ 25,99**.

---

# Resumo dos endpoints

| Método | Endpoint                         | Descrição                                        | Retorno |
| ------ | -------------------------------- | ------------------------------------------------ | ------- |
| `GET`  | `/api/chat-model`                | Envia um prompt diretamente ao modelo            | Texto   |
| `GET`  | `/client/chat`                   | Envia um prompt utilizando `ChatClient`          | Texto   |
| `POST` | `/speech/enviar`                 | Converte texto em áudio                          | MP3     |
| `POST` | `/audio/transcribe`              | Transcreve um arquivo de áudio                   | Texto   |
| `POST` | `/transactions`                  | Cadastra uma transação                           | JSON    |
| `GET`  | `/transactions/{category}`       | Lista transações por categoria                   | JSON    |
| `GET`  | `/transactions/todos/{category}` | Soma as transações e retorna o total em áudio    | MP3     |
| `POST` | `/transactions/ai`               | Processa comandos financeiros enviados por áudio | MP3     |

---

# Endpoints

## 1. Enviar prompt diretamente ao modelo

```http
GET /api/chat-model
```

### Controller

```text
ChatModelController
```

### Descrição

Envia um prompt diretamente para:

```java
OpenAiChatModel.call(prompt)
```

A resposta do modelo é retornada como texto.

### Query parameters

| Parâmetro | Tipo     | Obrigatório | Descrição               |
| --------- | -------- | ----------: | ----------------------- |
| `prompt`  | `String` |         Sim | Texto enviado ao modelo |

### Exemplo

```bash
curl "http://localhost:8080/api/chat-model?prompt=Ola"
```

### Resposta

```text
Olá! Como posso ajudar?
```

---

## 2. Enviar prompt utilizando ChatClient

```http
GET /client/chat
```

### Controller

```text
ChatClientController
```

### Descrição

Envia um prompt utilizando o bean `ChatClient`.

### Query parameters

| Parâmetro | Tipo     | Obrigatório | Descrição                   |
| --------- | -------- | ----------: | --------------------------- |
| `prompt`  | `String` |         Sim | Texto enviado ao assistente |

### Exemplo

```bash
curl "http://localhost:8080/client/chat?prompt=Explique%20orcamento%20pessoal"
```

### Resposta

```text
Orçamento pessoal é uma forma de organizar suas receitas e despesas...
```

---

## 3. Converter texto em áudio

```http
POST /speech/enviar
```

### Controller

```text
SpeechApiController
```

### Descrição

Recebe um texto em JSON e gera um arquivo MP3 utilizando `TextToSpeechModel`.

### Content-Type

```http
application/json
```

### Produces

```http
audio/mp3
```

### Corpo da requisição

| Campo  | Tipo     | Obrigatório | Descrição                          |
| ------ | -------- | ----------: | ---------------------------------- |
| `text` | `String` |         Sim | Texto que será convertido em áudio |

```json
{
  "text": "Seu resumo financeiro está pronto."
}
```

### Exemplo com cURL no Windows

```bash
curl -X POST "http://localhost:8080/speech/enviar" ^
  -H "Content-Type: application/json" ^
  -d "{\"text\":\"Seu resumo financeiro está pronto.\"}" ^
  --output audio.mp3
```

### Resposta

* Arquivo MP3.
* Header `Content-Disposition` com o nome `audio.mp3`.

```http
Content-Disposition: attachment; filename="audio.mp3"
```

---

## 4. Transcrever um arquivo de áudio

```http
POST /audio/transcribe
```

### Controller

```text
TranscriptionController
```

### Descrição

Recebe um arquivo de áudio e realiza sua transcrição utilizando `TranscriptionModel`.

### Content-Type

```http
multipart/form-data
```

### Campo multipart

| Campo  | Tipo    | Obrigatório | Descrição                            |
| ------ | ------- | ----------: | ------------------------------------ |
| `file` | Arquivo |         Sim | Arquivo de áudio que será transcrito |

### Exemplo

```bash
curl -X POST "http://localhost:8080/audio/transcribe" ^
  -F "file=@src/test/resources/audio/arquivo-1.m4a"
```

### Resposta

```text
Registre uma compra de vinte e cinco reais e noventa e nove centavos no mercado.
```

---

## 5. Criar uma transação

```http
POST /transactions
```

### Controller

```text
TransactionController
```

### Descrição

Cria uma nova transação financeira e persiste os dados no MySQL.

### Content-Type

```http
application/json
```

### Status de sucesso

```http
201 Created
```

### Corpo da requisição

| Campo         | Tipo     | Obrigatório | Descrição                      |
| ------------- | -------- | ----------: | ------------------------------ |
| `description` | `String` |         Sim | Descrição da transação         |
| `category`    | `String` |         Sim | Categoria da transação         |
| `amount`      | `Long`   |         Sim | Valor da transação em centavos |

```json
{
  "description": "Compra no mercado",
  "category": "GROCERIES",
  "amount": 2599
}
```

### Exemplo

```bash
curl -X POST "http://localhost:8080/transactions" ^
  -H "Content-Type: application/json" ^
  -d "{\"description\":\"Compra no mercado\",\"category\":\"GROCERIES\",\"amount\":2599}"
```

### Resposta esperada

```json
{
  "id": "uuid-da-transacao",
  "category": "GROCERIES",
  "description": "Compra no mercado",
  "amout": 2599.0
}
```

### Pontos de atenção

O record de resposta atualmente se chama:

```java
TransactionReponse
```

Existem dois possíveis problemas no código atual:

1. O nome `TransactionReponse` possui um erro de escrita. O nome recomendado seria:

```java
TransactionResponse
```

2. O campo `amout` também possui um erro de escrita. O nome recomendado seria:

```java
amount
```

Além disso, o método:

```java
TransactionReponse.from(...)
```

está passando `description` e `category` em posições invertidas para o construtor.

Por causa disso, a resposta real pode aparecer semelhante a:

```json
{
  "id": "uuid-da-transacao",
  "category": "Compra no mercado",
  "description": "GROCERIES",
  "amout": 2599.0
}
```

---

## 6. Listar transações por categoria

```http
GET /transactions/{category}
```

### Controller

```text
TransactionController
```

### Descrição

Lista todas as transações cadastradas em uma determinada categoria.

### Path parameters

| Parâmetro  | Tipo     | Obrigatório | Valores aceitos               |
| ---------- | -------- | ----------: | ----------------------------- |
| `category` | `String` |         Sim | `GROCERIES`, `FARM` ou `AUTO` |

### Status de sucesso

```http
200 OK
```

### Exemplo

```bash
curl "http://localhost:8080/transactions/GROCERIES"
```

### Resposta esperada

```json
[
  {
    "id": "uuid-da-transacao",
    "category": "GROCERIES",
    "description": "Compra no mercado",
    "amout": 2599.0
  }
]
```

### Ponto de atenção

A resposta pode apresentar os campos `category` e `description` invertidos devido ao problema existente no método:

```java
TransactionReponse.from(...)
```

---

## 7. Calcular total por categoria e gerar áudio

```http
GET /transactions/todos/{category}
```

### Controller

```text
TransactionController
```

### Descrição

O endpoint executa o seguinte fluxo:

1. Lista as transações da categoria.
2. Soma os valores em centavos.
3. Converte o total para reais.
4. Cria uma frase com o valor total.
5. Converte a frase em um arquivo MP3.

### Path parameters

| Parâmetro  | Tipo     | Obrigatório | Valores aceitos               |
| ---------- | -------- | ----------: | ----------------------------- |
| `category` | `String` |         Sim | `GROCERIES`, `FARM` ou `AUTO` |

### Produces

```http
audio/mp3
```

### Status de sucesso

```http
200 OK
```

### Exemplo

```bash
curl "http://localhost:8080/transactions/todos/GROCERIES" ^
  --output total.mp3
```

### Exemplo da frase gerada

```text
O valor total das suas contas da categoria GROCERIES é de R$ 25,99.
```

### Resposta

* Arquivo MP3.
* Header `Content-Disposition` com o nome `audio.mp3`.

```http
Content-Disposition: attachment; filename="audio.mp3"
```

---

## 8. Processar transação por inteligência artificial

```http
POST /transactions/ai
```

### Controller

```text
TransactionController
```

### Descrição

Recebe um comando de voz e utiliza inteligência artificial para interpretar e executar operações financeiras.

### Fluxo da operação

```mermaid
flowchart LR
    A[Arquivo de áudio] --> B[TranscriptionModel]
    B --> C[Texto transcrito]
    C --> D[ChatClient]
    D --> E[Assistente financeiro]
    E --> F[Ferramentas de transação]
    F --> G[Resposta textual]
    G --> H[TextToSpeechModel]
    H --> I[Arquivo MP3]
```

O endpoint realiza as seguintes etapas:

1. Recebe o arquivo de áudio.
2. Transcreve o conteúdo.
3. Envia a transcrição para o `ChatClient`.
4. Aplica as instruções do prompt de sistema.
5. Permite que o assistente utilize ferramentas para consultar ou persistir transações.
6. Converte a resposta do assistente em áudio MP3.

### Prompt de sistema

```text
src/main/resources/prompts/system.st
```

### Content-Type

```http
multipart/form-data
```

### Produces

```http
audio/mp3
```

### Campo multipart

| Campo  | Tipo    | Obrigatório | Descrição                                 |
| ------ | ------- | ----------: | ----------------------------------------- |
| `file` | Arquivo |         Sim | Arquivo de áudio com o comando financeiro |

### Exemplo

```bash
curl -X POST "http://localhost:8080/transactions/ai" ^
  -F "file=@src/test/resources/audio/arquivo-1.m4a" ^
  --output resposta.mp3
```

### Ferramentas disponíveis para o assistente

| Ferramenta                      | Descrição                                  |
| ------------------------------- | ------------------------------------------ |
| `perist-transaction`            | Persiste uma nova transação financeira     |
| `list-transactions-by-category` | Lista transações financeiras por categoria |

### Exemplo de comando por áudio

```text
Registre uma compra de noventa reais de frango na categoria mercado.
```

### Exemplo de resposta gerada

```text
Transação registrada: R$ 90,00 — Compra de frango, categoria GROCERIES.
```

### Resposta HTTP

* Arquivo MP3.
* Header `Content-Disposition` com o nome `audio.mp3`.

```http
Content-Disposition: attachment; filename="audio.mp3"
```

---

# Exemplos rápidos

## Criar uma transação

```bash
curl -X POST "http://localhost:8080/transactions" ^
  -H "Content-Type: application/json" ^
  -d "{\"description\":\"Combustível\",\"category\":\"AUTO\",\"amount\":15000}"
```

## Listar transações de mercado

```bash
curl "http://localhost:8080/transactions/GROCERIES"
```

## Obter o total da categoria em áudio

```bash
curl "http://localhost:8080/transactions/todos/GROCERIES" ^
  --output total-groceries.mp3
```

## Enviar uma transação por áudio

```bash
curl -X POST "http://localhost:8080/transactions/ai" ^
  -F "file=@src/test/resources/audio/arquivo-1.m4a" ^
  --output resposta.mp3
```

---

# Estrutura de status HTTP

|                      Status | Significado                              |
| --------------------------: | ---------------------------------------- |
|                    `200 OK` | Requisição processada com sucesso        |
|               `201 Created` | Transação criada com sucesso             |
|           `400 Bad Request` | Dados ou arquivo enviados incorretamente |
|             `404 Not Found` | Recurso ou endpoint não encontrado       |
| `500 Internal Server Error` | Erro interno durante o processamento     |

---
---

# Rotas disponíveis

```text
GET  /api/chat-model?prompt=...
GET  /client/chat?prompt=...

POST /speech/enviar
POST /audio/transcribe

POST /transactions
GET  /transactions/{category}
GET  /transactions/todos/{category}
POST /transactions/ai
```
