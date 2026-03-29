---
name: efms-identity-service
description: Identity, multi-company, role, and permission management for EFMS.
---

# EFMS Identity Service

The Identity service manages the organizational structure and access control (Auth and RBAC) for the entire EFMS application.

## Core Responsibilities
- **Authentication**: Issuing JWT tokens upon user login.
- **Multi-Company (Companies)**: Manages different organizational entities in a multi-tenant layout.
- **Roles & Permissions (RBAC)**: Fine-grained access control with users assigned to roles, and roles bound to permissions.
- **Users**: User profiles and status.
- **Audit Logs**: Tracking changes to identity data globally.

## Identity Database Schema
- `companies`: `id` (UUID), `name`, `address`, `currency`, `is_active`, `createdAt`.
- `roles`: `id` (UUID), `name`, `description`, `is_active`.
- `permissions`: `id` (UUID), `resource` (e.g., 'invoice'), `action` (e.g., 'create'), `description`.
- `role_permissions`: Mapping of `role_id` and `permission_id`.
- `users`: `id` (UUID), `company_id` (FK to companies), `role_id` (FK to roles), `name`, `email`, `password`, `is_active`.
- `audit_logs`: `id` (UUID), `table_name`, `record_id`, `action`, `changed_by`, `changed_at`, `old_data` (JSON), `new_data` (JSON).

## API Endpoints (v1)

**Context Path:** `http://localhost:8080/api/identity` (routed via API Gateway)

- **Auth Controller**:
  - `POST /auth/login`: Authenticate and return JWT token.
  - `POST /auth/register`: Create a new user account.
- **User Controller**:
  - `/v1/users`: Management of user profiles (`id`, `update`, `delete`, `getAllUsers`).
- **Role Controller**:
  - `/v1/roles`: CRUD operations for roles and assignment of permissions.
- **Permission Controller**:
  - `/v1/permissions`: CRUD for static system permissions patterns (`resource:action`).
- **Company Controller**:
  - `/v1/companies`: Create, Read, Update, Delete for companies.
- **Audit Log Controller**:
  - `/v1/audit-logs`: Access system change history with pagination (`page`, `size`).
  - `/v1/audit-logs/record`: Fetch changes for a specific `recordId` and `tableName`.

## Implementation Details

- **Package**: `com.linhdv.efms_identity_service`
- **Security**: JWT-based authentication using **jjwt 0.11.5**. User passwords hashed securely with **BCrypt**.
- **Data Mapping**: Mandatory use of **MapStruct** (`mapper` package) for `Entity` to `DTO` conversions.
- **Response Format**: All REST APIs must wrap their return data in the generic `ApiResponse<T>` (`wrapper` / `dto.common` package) object.
- **Validation**: Ensure `@Valid` is used for all incoming request payloads (`dto/request`).

## Code Structure Rules
- **`controller`**: REST API definitions. Minimal business logic. Returns `ApiResponse`.
- **`service` / `service.impl`**: Core business and validation logic.
- **`repository`**: Spring Data JPA interfaces. Includes `company_id` filtering.
- **`entity`**: JPA Mappings representing the Database schema.
- **`dto`**: POJOs for transferring data between Controller and Service layers (`request` and `response` packages).
- **`mapper`**: MapStruct interfaces (`@Mapper(componentModel = "spring")`).
- **`config` / `security`**: Setup filters, WebSecurity, JWT parser logic.

## Guidelines
1. When modifying `permissions`, ensure they follow the `resource:action` pattern (e.g. `user:create`).
2. Changes to any business entity MUST be intercepted or logged using the internal `AuditService`.
3. Multi-tenancy must be strictly applied on Users and Roles using `company_id`.
