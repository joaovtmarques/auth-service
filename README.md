# Auth Service (`auth-service`)

Microsserviço de autenticação e autorização centralizada desenvolvido em **Java 17** e **Spring Boot 3**, estruturado sob princípios de **Domain-Driven Design (DDD)** e **Clean Architecture**. O sistema provê cadastro e autenticação de usuários, emissão de tokens **JWT (Access Token)** com claims de perfis, rotação segura de **Refresh Tokens** com detecção de reuso, e gerenciamento completo de controle de acesso baseado em perfis (**RBAC**).

---

## Tecnologias Utilizadas

* **Linguagem & Runtime:** Java 17
* **Framework Principal:** Spring Boot 3.4.1
  * Spring Web (REST API)
  * Spring Security (Stateless JWT + `@EnableMethodSecurity`)
  * Spring Data JPA / Hibernate 6
  * Spring Validation (Jakarta Bean Validation)
* **Banco de Dados:** PostgreSQL 16 (`postgres:16-alpine` via Docker)
* **Autenticação & Criptografia:**
  * Auth0 `java-jwt` (emissão e validação de tokens JWT HMAC256)
  * BCrypt / `PasswordEncryptor` (hash de senhas)
  * SHA-256 / `HashService` (armazenamento seguro de refresh tokens via hash)
* **Testes:** JUnit 5, Mockito, Spring Security Test e `MockMvc`
* **Ferramentas Auxiliares:** Lombok, Maven, Docker & Docker Compose

---

## Arquitetura do Projeto

O projeto isola as regras de negócio de frameworks externos, dividindo-se em duas camadas principais sob `com.joaovtmarques.auth`:

```text
src/main/java/com/joaovtmarques/auth/
├── domain/                     # Núcleo da aplicação (Java puro, sem dependências de Spring/JPA)
│   ├── common/service/         # Interfaces utilitárias (ex: HashService)
│   ├── refresh_token/          # Domínio de Refresh Token (Model, Repository, Exceptions, Use Cases)
│   ├── role/                   # Domínio de Perfis/Roles (Model, Repository, Exceptions, Use Cases)
│   └── user/                   # Domínio de Usuários (Model, Repository, Services, Exceptions, Use Cases)
└── infra/                      # Adaptadores, Frameworks e Infraestrutura
    ├── config/                 # Injeção de beans de Use Cases e Seeder inicial (MasterAdminSeeder)
    ├── persistence/            # Entidades JPA, Repositórios Spring Data e Adapters
    ├── security/               # SecurityConfigurations, SecurityFilter e JwtTokenService
    └── web/                    # Controllers REST, DTOs (Records) e GlobalExceptionHandler
```

---

## Funcionalidades e Regras de Negócio

### 1. Gestão de Usuários e Autenticação
* **Cadastro (`POST /auth/register`):** Cria novos usuários validando unicidade de e-mail e telefone, criptografando a senha antes da persistência.
* **Login (`POST /auth/login`):** Autentica credenciais e retorna um par de tokens:
  * **`accessToken` (JWT):** Contém `iss` (`auth-service`), `sub` (`userId` UUID), claim `roles` (lista de perfis atribuídos) e tempo de expiração configurável (padrão: 15 minutos).
  * **`refreshToken`:** Token opaco (UUID) armazenado no banco apenas em formato de hash, vinculado ao IP e `User-Agent` da sessão, com validade de 7 dias.

### 2. Rotação de Refresh Tokens e Segurança de Sessão
* **Rotação (`POST /auth/refresh`):** Valida o `refreshToken` recebido, emite um novo par (`accessToken` + `refreshToken`), atualiza as claims de `roles` caso o perfil do usuário tenha sido alterado e invalida o token anterior (`revokedAt` e `replacedByTokenHash`).
* **Detecção de Reuso (Token Reuse Detection):** Caso um `refreshToken` já revogado seja reutilizado, o sistema interpreta como possível comprometimento de sessão, revoga/deleta **todos** os refresh tokens daquele usuário e retorna `401 Unauthorized`.
* **Logout Individual (`POST /auth/logout`):** Revoga apenas o `refreshToken` da sessão atual.
* **Logout Global (`POST /auth/logout-all`):** Encerra todas as sessões ativas do usuário autenticado.

### 3. Controle de Acesso Baseado em Perfis (RBAC)
* **Bootstrap Automático (`MasterAdminSeeder`):** Na inicialização da aplicação, caso ainda não exista, o sistema cria automaticamente a role `AUTH_MASTER` e o usuário administrador mestre configurado nas variáveis de ambiente.
* **Normalização de Nomes:** Todas as roles são armazenadas em maiúsculas e sem espaços nas extremidades (ex: `" sargenteante "` -> `"SARGENTEANTE"`).
* **Proteção de Rotas:** Todos os endpoints de gerenciamento de roles (`/roles` e `/users/{userId}/roles`) são protegidos por `@PreAuthorize("hasRole('AUTH_MASTER')")`.
* **Proteção contra Lockout:** O sistema impede a remoção da role `AUTH_MASTER` de um usuário caso ele seja o último administrador mestre ativo no banco (`LastMasterAdminRemovalException` -> `409 Conflict`).

---

## Endpoints da API

### Autenticação e Sessões (`/auth`)

| Método | Rota | Acesso | Descrição | Status de Retorno |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/auth/register` | Público | Registra um novo usuário no sistema | `201 Created` / `409 Conflict` |
| `POST` | `/auth/login` | Público | Autentica usuário e retorna `accessToken` e `refreshToken` | `200 OK` / `401 Unauthorized` |
| `POST` | `/auth/refresh` | Público | Rotaciona o `refreshToken` e emite novo `accessToken` | `200 OK` / `401 Unauthorized` |
| `POST` | `/auth/logout` | Público | Revoga um `refreshToken` específico | `204 No Content` |
| `POST` | `/auth/logout-all` | Autenticado (`Bearer`) | Revoga todos os refresh tokens do usuário logado | `204 No Content` / `401 Unauthorized` |

### Gerenciamento de Perfis / RBAC (Requer `ROLE_AUTH_MASTER`)

| Método | Rota | Descrição | Status de Retorno |
| :--- | :--- | :--- | :--- |
| `POST` | `/roles` | Cria um novo perfil de acesso | `201 Created` / `403 Forbidden` / `409 Conflict` |
| `GET` | `/roles` | Lista todos os perfis cadastrados | `200 OK` / `403 Forbidden` |
| `POST` | `/users/{userId}/roles` | Atribui uma role existente a um usuário | `204 No Content` / `404 Not Found` / `403 Forbidden` |
| `DELETE` | `/users/{userId}/roles/{roleId}` | Remove uma role de um usuário | `204 No Content` / `404 Not Found` / `409 Conflict` |

---

## Exemplos de Requisições (Payloads)

### `POST /auth/register`
```json
{
  "name": "João Vitor",
  "email": "joao@email.com",
  "phone": "12999999999",
  "password": "SenhaForte123"
}
```

### `POST /auth/login`
```json
{
  "email": "master@auth.local",
  "password": "Master@123456"
}
```
**Resposta (`200 OK`):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "640c887f-a70a-4f8c-a192-959060ddd9a5"
}
```

### `POST /auth/refresh`
```json
{
  "refreshToken": "640c887f-a70a-4f8c-a192-959060ddd9a5"
}
```

### `POST /roles` (Header: `Authorization: Bearer <accessToken>`)
```json
{
  "name": "SARGENTEANTE",
  "description": "Perfil operacional para controle de escalas e viaturas"
}
```

### `POST /users/{userId}/roles` (Header: `Authorization: Bearer <accessToken>`)
```json
{
  "roleId": "9d36dcc3-0267-42f9-97a2-058988b2a0c1"
}
```

---

## Tratamento Global de Erros

As exceções de domínio, validação e segurança são padronizadas pelo `GlobalExceptionHandler`:

| Status HTTP | Exceções Mapeadas | Cenário |
| :--- | :--- | :--- |
| `400 Bad Request` | `MethodArgumentNotValidException` | Campos obrigatórios ausentes ou formato inválido |
| `401 Unauthorized` | `InvalidCredentialsException`, `InvalidRefreshTokenException` | Senha incorreta, token expirado ou reuso detectado |
| `403 Forbidden` | `AccessDeniedException` / `AuthorizationDeniedException` | Usuário autenticado sem a role `AUTH_MASTER` |
| `404 Not Found` | `UserNotFoundException`, `RoleNotFoundException` | Usuário ou role inexistente |
| `409 Conflict` | `UserAlreadyExistsException`, `RoleAlreadyExistsException`, `LastMasterAdminRemovalException` | E-mail/telefone/role duplicados ou tentativa de remover o último `AUTH_MASTER` |
| `500 Internal Server Error` | `Exception` | Falhas internas não previstas |

---

## Configuração e Execução Local

### 1. Variáveis de Ambiente (`application.yml`)

| Variável | Padrão | Descrição |
| :--- | :--- | :--- |
| `DB_HOST` | `localhost` | Host do PostgreSQL |
| `DB_PORT` | `5432` | Porta do PostgreSQL |
| `DB_NAME` | `auth_db` | Nome do banco de dados |
| `DB_USER` | `postgres` | Usuário do banco |
| `DB_PASSWORD` | `12345678` | Senha do banco |
| `JWT_SECRET` | *(chave padrão dev)* | Chave secreta HMAC256 para assinatura dos JWTs |
| `JWT_EXPIRATION_MINUTES` | `15` | Tempo de vida do Access Token em minutos |
| `api.security.master.email` | `master@auth.local` | E-mail do administrador mestre inicial |
| `api.security.master.password` | `Master@123456` | Senha do administrador mestre inicial |

### 2. Subindo o Banco de Dados e a Aplicação

```bash
# 1. Subir o container PostgreSQL
docker compose up -d

# 2. Executar os testes unitários e de camada web
mvn test

# 3. Iniciar a aplicação na porta 8080
mvn spring-boot:run
```

---

## Suíte de Testes

* **Testes Unitários de Domínio (JUnit 5 + Mockito):**
  * `CreateRoleUseCaseImplTest`, `ListRolesUseCaseImplTest`, `AssignRoleToUserUseCaseImplTest`, `RemoveRoleFromUserUseCaseImplTest` (incluindo proteção do último `AUTH_MASTER`).
  * `AuthenticateUserUseCaseTest` (validação de credenciais e emissão de claims de roles).
  * `RotateRefreshTokenUseCaseImplTest` (rotação válida, token expirado e revogação global em caso de reuso).
* **Testes de Integração Web (`@WebMvcTest` + `MockMvc`):**
  * `RoleControllerTest` (validação de contratos HTTP, códigos de status, serialização JSON e bloqueios `@PreAuthorize` via `@WithMockUser`).