---
name: efms-identity-service
description: Identity, multi-company, role, and permission management for EFMS.
---

# EFMS Identity Service

The Identity service is responsible for managing the organizational structure and access control for the entire EFMS application.

## Core Responsibilities
- **Multi-Company (Companies)**: Manages different organizational entities.
- **Roles & Permissions**: Fine-grained access control (RBAC).
- **Users**: User profiles and authentication metadata.
- **Audit Logs**: Tracking changes to identity data.

## Identity Database Schema

Refer to the following tables in the Identity database:
- `companies`: id (UUID), name, tax_code, address, currency, is_active, created_at.
- `roles`: id (UUID), name, description, is_active.
- `permissions`: id (UUID), resource (e.g., 'invoice'), action (e.g., 'create'), description.
- `role_permissions`: role_id, permission_id.
- `users`: id (UUID), company_id (FK to companies), role_id (FK to roles), name, email, password, is_active.

## Implementation Details
- **Package**: `com.linhdv.efms_identity_service`
- **Security**: Focus on secure password hashing and generating JWT claims for the gateway.
- **Multi-tenancy**: All users and access rules are tied to a `company_id`.

## Guidelines
1. When modifying `permissions`, ensure they follow the `resource:action` pattern.
2. Changes to `users` or `roles` MUST be logged in the `audit_logs` table.
3. Use JPA/Hibernate for database interaction.
