---
name: efms-database
description: Reference for the EFMS (Enterprise Financial Management System) PostgreSQL v4 schema.
---

# EFMS Database Schema (v4)

This skill provides a reference for the PostgreSQL v4 database schema used in the EFMS application.

## Schema Overview
The database is divided into two logical sections corresponding to the two main services: Identity and Core.

### Section 1: EFMS Identity Service
Database responsible for organizations, users, and permissions.

- `companies`: Multi-company support.
- `roles`: Access roles (e.g., Admin, Accountant).
- `permissions`: Individual resource permissions (e.g., invoice:read).
- `role_permissions`: Role-based mapping.
- `users`: Registered users.
- `audit_logs` (Identity): Changes to identity data.

### Section 2: EFMS Core Service
Database containing accounting and financial data. Note that foreign keys to Identity entities (e.g., `company_id`, `created_by`) are NOT enforced at the database level.

- `fiscal_periods`: Accounting cycles.
- `accounts`: Chart of accounts.
- `partners`: Customers and vendors.
- `journal_entries`: Accounting document header.
- `journal_lines`: Double-entry transaction lines.
- `invoices`: Sales (AR) and Purchase (AP) invoices.
- `invoice_lines`: Itemized billing details.
- `payments`: Cash or bank payment records.
- `invoice_payments`: Many-to-many link between invoices and payments.
- `bank_accounts`: Bank and cash GL accounts.
- `bank_transactions`: Bank statement records.
- `audit_logs` (Core): Changes to financial data.

## Key Principles
- **UUID Keys**: All IDs use the `gen_random_uuid()` function.
- **Audit Logging**: Use `JSONB` for `old_data` and `new_data` columns in audit tables.
- **Precision**: Monetary amounts use `NUMERIC(18,2)`.
- **Indexing**: Frequent filters like `company_id`, `entry_date`, and `status` should always have an index.
