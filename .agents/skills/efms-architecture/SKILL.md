---
name: efms-architecture
description: General architecture and standards for the EFMS (Enterprise Financial Management System) backend.
---

# EFMS Backend Architecture

This skill provides an overview of the EFMS backend architecture, which consists of three main microservices working together to provide enterprise financial management features.

1. **efms-api-gateway (Port: 8080)**: The central entry point for all client requests, responsible for dynamic routing and global security (JWT validation).
2. **efms-identity-service (Port: 8081 - typical)**: Manages authentication, multi-tenant company structure, users, roles, and permissions (RBAC).
3. **efms-core-service (Port: 8082 - typical)**: Handles all financial and accounting operations (Invoices, Payments, Ledgers, Bank Accounts).

## Key Architectural Principles

- **Microservices Boundary**: Identity and Core services operate on separate, isolated databases or distinct schemas.
- **Routing Strategy**: 
  - All external requests are sent to API Gateway on port `8080`.
  - Prefix `http://localhost:8080/api/identity/**` routes to `efms-identity-service`.
  - Prefix `http://localhost:8080/api/core/**` routes to `efms-core-service`.
- **Loose Coupling**: Services communicate via REST APIs or Feign Client. Core service references Identity entities (users, companies) exclusively bypassing their UUIDs. There are NO hard database-level foreign keys across microservices.
- **Multi-tenancy**: Every business entity is linked to a `company_id`. Queries must isolate data by this ID.
- **Authentication**: JWT-based authentication. Client calls `/api/identity/auth/login` to retrieve a Token, which is then validated by `efms-api-gateway` in subsequent requests before being forwarded to downstream services.
- **Standard API Responses**: All REST APIs must use the generic `ApiResponse<T>` wrapper for consistency (`status`, `message`, `data`).

## Technology Stack

- **Language**: Java 21
- **Framework**: Spring Boot 3.3.x, Spring Cloud (Gateway, Config)
- **Build Tool**: Maven
- **Database**: PostgreSQL (v4 Schema Documented in `doc/`)
- **Mandatory Libraries**: **Lombok** (minimizing boilerplate), **MapStruct** (for Entity-to-DTO conversion), **JJWT** (Security).

## Package Naming Convention
- `com.linhdv.efms_api_gateway`
- `com.linhdv.efms_identity_service`
- `com.linhdv.efms_core_service`

## Development Guidelines
1. Always include `companyId` in service methods and queries to ensure strict data isolation.
2. Use `UUID` for all Primary Keys (`id`) and Foreign Keys referencing other services.
3. Keep Controllers thin by delegating complex business logic to Services.
4. Ensure proper audit logging for all data manipulations using the internal Audit service.
5. Provide detailed Swagger/OpenAPI annotations on Controllers.
