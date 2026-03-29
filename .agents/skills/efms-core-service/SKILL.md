---
name: efms-core-service
description: Financial and accounting operations for EFMS.
---

# EFMS Core Service

The Core service handles all major financial accounting operations, double-entry ledgers, and cash flows.

## Core Responsibilities
- **General Ledger**: Management of the Chart of Accounts (COA) and `journal_entries` for double-entry accounting.
- **Accounts Receivable (AR) & Accounts Payable (AP)**: Handling `invoices` (billing) and `payments` (cash receipts and disbursements).
- **Cash & Bank**: Management of `bank_accounts` and reconciliation of `bank_transactions`.
- **Entities**: Administration of `partners` (customers and vendors) and their balances.
- **Reporting**: Trial balances, aging reports, etc.

## Core Database Schema
- `fiscal_periods`: Accounting cycles (open/closed).
- `accounts`: Chart of accounts (asset, liability, equity, revenue, expense).
- `partners`: Customers and vendors.
- `journal_entries` & `journal_lines`: Double-entry accounting records linking to `accounts`.
- `invoices` & `invoice_lines`: Receivables and payables tracking.
- `payments`: Bank/cash operations mapped to invoices or direct journals.
- `bank_accounts` & `bank_transactions`: Bank statements and reconciliation data.

## API Endpoints (v1)

**Context Path:** `http://localhost:8080/api/core` (routed via API Gateway)

- **Partners**:
  - `/v1/partners`
- **Bank Accounts**:
  - `/v1/finance/bank-accounts`
- **Accounts**:
  - `/v1/accounting/accounts`: Chart of Accounts operations.
- **Journal Entries**:
  - `/v1/accounting/journals`: Create/Update (`draft`), Detail, Delete (`draft`).
- **Invoices**:
  - `/v1/invoices`: CRUD for AP/AR. Draft state enables deletion.
- **Payments**:
  - `/v1/payments`: General CRUD operations.
  - `POST /v1/payments/{id}/post`: Post payment to the General Ledger (GL). Unlocks financial impact.
  - `POST /v1/payments/{id}/allocate`: Allocate previously received/paid amounts directly to open `Invoices`.
- **Other Tags Identified**: *Bank Reconciliation, Bank Transactions, Fiscal Periods, Trial Balance, Reports.*

## Accounting Rules
- **Double-Entry**: Every `journal_entry` must generate at least two `journal_lines` where total debits strictly equal total credits (`debit = credit`).
- **Draft vs Posted**: Transactions (Journals, Invoices, Payments) start as `draft` and strictly require a `post` step (`/post`) to officially impact account balances. 
- **Currency Calculations**: `BigDecimal` must be used for monetary precision. Exchange rates must be handled if the ledger currency is `VND` but foreign transactions persist.

## Implementation Details

- **Package**: `com.linhdv.efms_core_service`
- **Identity Links**: `company_id`, `created_by`, `updated_by` are `UUID` strings referencing remote entities in `efms-identity-service`. Core handles isolation through `companyId` filtering manually mapped on the service layer, bypassing database-level FK limits.
- **Data Mapping**: Use `MapStruct` extensively for `Entity` to `DTO` conversions. Focus on `ApiResponse<T>` output format.

## Code Structure Rules
- **`controller`**: Grouped logically: `controller.accounting` for accounts/journals, `controller.finance` for banks, etc. Returns structured `ApiResponse`.
- **`service`**: Validation logic e.g., checking if `fiscal_period` is `open` before posting.
- **`repository`**: Database connections requiring `companyId`.
- **`entity` / `dto` (request, response) / `mapper`**: Similar architecture to Identity Service.

## Guidelines
1. Do NOT execute a transaction if the `fiscal_period` of the transaction dates lies outside an `open` period.
2. Changes to any financial data MUST be documented locally in the `audit_logs` table (specific to Core DB).
3. Validate `@Valid` annotations on `xxxRequest` DTOs.
