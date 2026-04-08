> 📋 **DB PostgreSQL v4 (Microservices)** — Cập nhật theo hệ thống EFMS tách làm 2 service là Identity và Core. Bổ sung `roles`, `permissions` cho chặt chẽ và tắt bỏ các ràng buộc khóa ngoại (Foreign Key) giữa hai service.

---

# PHẦN 1: EFMS IDENTITY SERVICE DATABASE

Database này chịu trách nhiệm quản lý tổ chức, người dùng và phân quyền. (Chạy trên schema/database riêng).

## Danh sách bảng (6 bảng)
| # | Bảng | Mô tả |
| --- | --- | --- |
| 1 | companies | Công ty (multi-company) |
| 2 | roles | Danh mục vai trò |
| 3 | permissions | Danh mục quyền hạn |
| 4 | role_permissions | Bảng trung gian Role - Permission |
| 5 | users | Người dùng |
| 6 | audit_logs | Nhật ký thay đổi hệ thống danh tính |

### 0. Extensions
```sql
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
```

### 1. Companies — Công ty
```sql
CREATE TABLE companies (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name         VARCHAR(255) NOT NULL,
    tax_code     VARCHAR(50),
    address      TEXT,
    currency     VARCHAR(3) NOT NULL DEFAULT 'VND',
    is_active    BOOLEAN NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP DEFAULT now()
);
```

### 2. Roles — Danh mục vai trò
```sql
CREATE TABLE roles (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name         VARCHAR(100) NOT NULL,
    description  TEXT,
    is_active    BOOLEAN NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP DEFAULT now()
);
```

### 3. Permissions — Danh mục quyền hạn
```sql
CREATE TABLE permissions (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    resource     VARCHAR(100) NOT NULL, -- vd: invoice, payment, user
    action       VARCHAR(50) NOT NULL,  -- vd: create, read, update, delete
    description  TEXT,
    created_at   TIMESTAMP DEFAULT now()
);
```

### 4. Role Permissions — Quyền của vai trò
```sql
CREATE TABLE role_permissions (
    role_id      UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    created_at   TIMESTAMP DEFAULT now(),
    PRIMARY KEY (role_id, permission_id)
);
```

### 5. Users — Người dùng
```sql
CREATE TABLE users (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id   UUID NOT NULL REFERENCES companies(id), -- Tham chiếu FK nội bộ bình thường
    role_id      UUID NOT NULL REFERENCES roles(id),
    name         VARCHAR(255) NOT NULL,
    email        VARCHAR(255) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    is_active    BOOLEAN NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP DEFAULT now()
);
```

### 6. Audit Logs (Identity) — Nhật ký hệ thống Identity
```sql
CREATE TABLE audit_logs (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    table_name  VARCHAR(100) NOT NULL,
    record_id   UUID NOT NULL,
    action      VARCHAR(20) NOT NULL,   -- INSERT / UPDATE / DELETE
    changed_by  UUID REFERENCES users(id),
    changed_at  TIMESTAMP DEFAULT now(),
    old_data    JSONB,
    new_data    JSONB
);
```

---

# PHẦN 2: EFMS CORE SERVICE DATABASE

Database này chứa dữ liệu kế toán (12 bảng). 
> ⚠️ **LƯU Ý QUAN TRỌNG:** Tất cả các liên kết tới `company_id`, `created_by`, `posted_by`, `closed_by`, `changed_by` sẽ bị xóa bỏ giới hạn `REFERENCES`. Chúng chỉ được lưu dưới dạng UUID trơn, phía ứng dụng Core sẽ tự kiểm tra tính hợp lệ bằng Token hoặc API.

## Danh sách bảng (12 bảng)
| # | Bảng | Mô tả |
| --- | --- | --- |
| 7 | fiscal_periods | Kỳ kế toán |
| 8 | accounts | Danh mục tài khoản kế toán |
| 9 | partners | Khách hàng & nhà cung cấp |
| 10 | journal_entries | Chứng từ kế toán |
| 11 | journal_lines | Dòng bút toán |
| 12 | invoices | Hóa đơn AR + AP Bill |
| 13 | invoice_lines | Chi tiết hóa đơn |
| 14 | payments | Thanh toán thu/chi |
| 15 | invoice_payments | Liên kết invoice ↔ payment |
| 16 | bank_accounts | Tài khoản ngân hàng / tiền mặt |
| 17 | bank_transactions | Giao dịch ngân hàng |
| 18 | audit_logs | Nhật ký thay đổi dữ liệu kế toán |

### 7. Fiscal Periods — Kỳ kế toán
```sql
CREATE TABLE fiscal_periods (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id   UUID NOT NULL, -- UUID từ Identity Service
    name         VARCHAR(50) NOT NULL,
    start_date   DATE NOT NULL,
    end_date     DATE NOT NULL,
    status       VARCHAR(20) NOT NULL DEFAULT 'open',  -- open / closed
    closed_by    UUID, -- UUID từ Identity Service
    closed_at    TIMESTAMP,
    created_at   TIMESTAMP DEFAULT now()
);
```

### 8. Accounts — Danh mục tài khoản kế toán
```sql
CREATE TABLE accounts (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id   UUID NOT NULL, -- UUID từ Identity Service
    code         VARCHAR(20) NOT NULL,
    name         VARCHAR(255) NOT NULL,
    type         VARCHAR(50) NOT NULL,   -- asset / liability / equity / revenue / expense
    balance_type VARCHAR(10) NOT NULL,   -- debit / credit
    parent_id    UUID REFERENCES accounts(id),
    is_active    BOOLEAN NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP DEFAULT now(),
    UNIQUE(company_id, code)
);
```

### 9. Partners — Khách hàng & Nhà cung cấp
```sql
CREATE TABLE partners (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id    UUID NOT NULL, -- UUID từ Identity Service
    name          VARCHAR(255) NOT NULL,
    type          VARCHAR(20) NOT NULL,   -- customer / vendor / both
    tax_code      VARCHAR(50),
    phone         VARCHAR(50),
    email         VARCHAR(255),
    address       TEXT,
    ar_account_id UUID REFERENCES accounts(id),  -- TK phải thu mặc định
    ap_account_id UUID REFERENCES accounts(id),  -- TK phải trả mặc định
    is_active     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP DEFAULT now()
);
```

### 10. Journal Entries — Chứng từ kế toán
```sql
CREATE TABLE journal_entries (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id    UUID NOT NULL, -- UUID từ Identity Service
    period_id     UUID REFERENCES fiscal_periods(id),
    entry_date    DATE NOT NULL,
    reference     VARCHAR(255),
    description   TEXT,
    status        VARCHAR(20) NOT NULL DEFAULT 'draft',  -- draft / posted / cancelled
    source        VARCHAR(50),   -- manual / ar / ap / payment / bank
    source_ref_id UUID,
    created_by    UUID, -- UUID từ Identity Service
    posted_by     UUID, -- UUID từ Identity Service
    posted_at     TIMESTAMP,
    created_at    TIMESTAMP DEFAULT now()
);
```

### 11. Journal Lines — Dòng bút toán
```sql
CREATE TABLE journal_lines (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    journal_entry_id UUID NOT NULL REFERENCES journal_entries(id) ON DELETE CASCADE,
    account_id       UUID NOT NULL REFERENCES accounts(id),
    partner_id       UUID REFERENCES partners(id),
    debit            NUMERIC(18,2) NOT NULL DEFAULT 0,
    credit           NUMERIC(18,2) NOT NULL DEFAULT 0,
    currency_code    VARCHAR(3) NOT I NULL DEFAULT 'VND',
    amount_currency  NUMERIC(18,2),
    exchange_rate    NUMERIC(18,6) DEFAULT 1,
    description      TEXT,
    created_at       TIMESTAMP DEFAULT now(),
    CONSTRAINT chk_debit_positive  CHECK (debit >= 0),
    CONSTRAINT chk_credit_positive CHECK (credit >= 0),
    CONSTRAINT chk_not_both        CHECK (NOT (debit > 0 AND credit > 0))
);
```

### 12. Invoices — Hóa đơn
```sql
CREATE TABLE invoices (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id       UUID NOT NULL, -- UUID từ Identity Service
    partner_id       UUID NOT NULL REFERENCES partners(id),
    invoice_type     VARCHAR(5) NOT NULL,   -- ar / ap
    invoice_number   VARCHAR(100),
    invoice_date     DATE NOT NULL,
    due_date         DATE,
    currency_code    VARCHAR(3) NOT NULL DEFAULT 'VND',
    exchange_rate    NUMERIC(18,6) DEFAULT 1,
    subtotal         NUMERIC(18,2) NOT NULL DEFAULT 0,
    tax_amount       NUMERIC(18,2) NOT NULL DEFAULT 0,
    total_amount     NUMERIC(18,2) NOT NULL DEFAULT 0,
    paid_amount      NUMERIC(18,2) NOT NULL DEFAULT 0,
    status           VARCHAR(20) NOT NULL DEFAULT 'draft',  -- draft / open / partial / paid / cancelled
    approval_status  VARCHAR(20) DEFAULT NULL,              -- pending / approved / rejected
    approval_comment TEXT DEFAULT NULL,
    camunda_process_id VARCHAR(255) DEFAULT NULL,
    journal_entry_id UUID REFERENCES journal_entries(id),
    created_by       UUID, -- UUID từ Identity Service
    created_at       TIMESTAMP DEFAULT now()
);
```

### 13. Invoice Lines — Chi tiết hóa đơn
```sql
CREATE TABLE invoice_lines (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id   UUID NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
    account_id   UUID REFERENCES accounts(id),
    description  TEXT NOT NULL,
    quantity     NUMERIC(10,2) NOT NULL DEFAULT 1,
    unit_price   NUMERIC(18,2) NOT NULL DEFAULT 0,
    tax_rate     NUMERIC(5,2) NOT NULL DEFAULT 0,
    tax_amount   NUMERIC(18,2) NOT NULL DEFAULT 0,
    amount       NUMERIC(18,2) NOT NULL DEFAULT 0
);
```

### 14. Payments — Thanh toán thu/chi
```sql
CREATE TABLE payments (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id       UUID NOT NULL, -- UUID từ Identity Service
    partner_id       UUID REFERENCES partners(id),
    payment_type     VARCHAR(10) NOT NULL,   -- receive / pay
    payment_date     DATE NOT NULL,
    currency_code    VARCHAR(3) NOT NULL DEFAULT 'VND',
    exchange_rate    NUMERIC(18,6) DEFAULT 1,
    amount           NUMERIC(18,2) NOT NULL,
    payment_method   VARCHAR(50),   -- cash / bank_transfer / check
    bank_account_id  UUID REFERENCES bank_accounts(id),
    reference        VARCHAR(255),
    journal_entry_id UUID REFERENCES journal_entries(id),
    created_by       UUID, -- UUID từ Identity Service
    created_at       TIMESTAMP DEFAULT now()
);
```

### 15. Invoice Payments — Liên kết Invoice ↔ Payment
```sql
CREATE TABLE invoice_payments (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id       UUID NOT NULL REFERENCES invoices(id),
    payment_id       UUID NOT NULL REFERENCES payments(id),
    allocated_amount NUMERIC(18,2) NOT NULL,
    created_at       TIMESTAMP DEFAULT now(),
    UNIQUE(invoice_id, payment_id)
);
```

### 16. Bank Accounts — Tài khoản ngân hàng / tiền mặt
```sql
CREATE TABLE bank_accounts (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id      UUID NOT NULL, -- UUID từ Identity Service
    name            VARCHAR(255) NOT NULL,
    bank_name       VARCHAR(255),
    account_number  VARCHAR(100),
    type            VARCHAR(20) NOT NULL,   -- cash / bank
    currency_code   VARCHAR(3) NOT NULL DEFAULT 'VND',
    opening_balance NUMERIC(18,2) DEFAULT 0,
    gl_account_id   UUID REFERENCES accounts(id),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT now()
);
```

### 17. Bank Transactions — Giao dịch ngân hàng
```sql
CREATE TABLE bank_transactions (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bank_account_id  UUID NOT NULL REFERENCES bank_accounts(id),
    transaction_date DATE NOT NULL,
    description      TEXT,
    type             VARCHAR(10) NOT NULL,   -- in / out
    amount           NUMERIC(18,2) NOT NULL,
    reference        VARCHAR(255),
    is_reconciled    BOOLEAN NOT NULL DEFAULT FALSE,
    journal_entry_id UUID REFERENCES journal_entries(id),
    created_at       TIMESTAMP DEFAULT now()
);
```

### 18. Audit Logs (Core) — Nhật ký thay đổi Core
```sql
CREATE TABLE audit_logs (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    table_name  VARCHAR(100) NOT NULL,
    record_id   UUID NOT NULL,
    action      VARCHAR(20) NOT NULL,   -- INSERT / UPDATE / DELETE
    changed_by  UUID, -- UUID từ Identity Service
    changed_at  TIMESTAMP DEFAULT now(),
    old_data    JSONB,
    new_data    JSONB
);
```

---

# Indexes Đề xuất

```sql
-- Identity Service
CREATE INDEX idx_users_company                    ON users(company_id);
CREATE INDEX idx_users_role                       ON users(role_id);
CREATE INDEX idx_audit_logs_identity_record       ON audit_logs(table_name, record_id);

-- Core Service
CREATE INDEX idx_accounts_company                 ON accounts(company_id);
CREATE INDEX idx_partners_company                 ON partners(company_id);
CREATE INDEX idx_journal_entries_company          ON journal_entries(company_id);
CREATE INDEX idx_journal_entries_period           ON journal_entries(period_id);
CREATE INDEX idx_journal_entries_date             ON journal_entries(entry_date);
CREATE INDEX idx_invoices_company                 ON invoices(company_id);
CREATE INDEX idx_invoices_partner                 ON invoices(partner_id);
CREATE INDEX idx_invoices_type_status             ON invoices(invoice_type, status);
CREATE INDEX idx_invoices_due_date                ON invoices(due_date);
CREATE INDEX idx_payments_company                 ON payments(company_id);
CREATE INDEX idx_bank_transactions_account        ON bank_transactions(bank_account_id);
CREATE INDEX idx_bank_transactions_reconciled     ON bank_transactions(is_reconciled);
CREATE INDEX idx_audit_logs_core_record           ON audit_logs(table_name, record_id);
```
