---
name: efms-architecture
description: General architecture and standards for the EFMS (Enterprise Financial Management System) backend.
---

# EFMS Backend Architecture

This skill provides an overview of the EFMS backend architecture, which consists of three main microservices:

1. **efms-api-gateway**: The entry point for all client requests, responsible for routing and global security.
2. **efms-identity-service**: Manages identities, multi-company structure, roles, and permissions.
3. **efms-core-service**: Handles all financial and accounting operations (Invoices, Payments, Ledgers).

## Key Architectural Principles

- **Microservices Boundary**: Identity and Core services have separate databases/schemas.
- **Loose Coupling**: Services communicate via REST APIs. Core service references Identity entities (users, companies) using their UUIDs without hard database-level foreign keys across services.
- **Multi-tenancy**: Every entity belongs to a `company_id`.
- **Authentication**: JWT-based authentication orchestrated by the API Gateway.
- **Technology Stack**:
    - **Language**: Java 17+
    - **Framework**: Spring Boot 3.x
    - **Build Tool**: Maven
    - **Database**: PostgreSQL

## Package Naming Convention
- `com.linhdv.efms_identity_service`
- `com.linhdv.efms_core_service`
- `com.linhdv.efms_api_gateway`

## Rules for Development
1. Always include `company_id` in queries to ensure data isolation.
2. Use UUID for all primary and foreign keys.
3. Ensure proper audit logging for sensitive financial transactions.
