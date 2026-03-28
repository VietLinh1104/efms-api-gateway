---
name: efms-core-service
description: Financial and accounting operations for EFMS.
---

# EFMS Core Service

The Core service is the heart of the EFMS system, handling all accounting, invoicing, and financial transactions.

## Core Responsibilities
- **General Ledger**: Management of accounts and journal entries.
- **Accounts Receivable (AR) & Accounts Payable (AP)**: Invoices and payments for partners.
- **Bank Reconciliation**: Bank accounts and transactions.
- **Fiscal Periods**: Management of accounting cycles.

## Core Database Schema

Refer to the following tables in the Core database:
- `fiscal_periods`: Accounting cycles (open/closed).
- `accounts`: Chart of accounts (asset, liability, equity, revenue, expense).
- `partners`: Customers and vendors.
- `journal_entries` & `journal_lines`: Double-entry accounting records.
- `invoices` & `invoice_lines`: Billing and invoicing.
- `payments`: Cash/bank payments and receipts.
- `bank_accounts` & `bank_transactions`: Bank statement management.

## Accounting Rules
- **Double-Entry**: Every `journal_entry` must have at least two `journal_lines` where total debits equal total credits.
- **Draft vs Posted**: Journal entries start as `draft` and must be `posted` to impact account balances.
- **Currency**: Default currency is `VND` (Vietnamese Dong), but support for foreign currencies via `exchange_rate` is required.

## Implementation Details
- **Package**: `com.linhdv.efms_core_service`
- **Identity Links**: `company_id`, `created_by`, and other user references are UUIDs that match data in the Identity service. There are NO hard foreign keys between these services at the DB level.
- **Calculations**: Use `BigDecimal` for all monetary values.

## Guidelines
1. Always validate that a `fiscal_period` is `open` before allowing any postings within its date range.
2. Ensure every transaction is linked to a `company_id`.
3. Changes to financial data MUST be recorded in the `audit_logs` (Core) table.
