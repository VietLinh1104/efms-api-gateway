-- ==============================================================================
-- MIGRATION SCRIPT: Cập nhật DB v3 hiện tại (Monolith) sang cấu trúc v4 (Microservices)
-- ==============================================================================

-- ------------------------------------------------------------------------------
-- PHẦN 1: THÊM CÁC BẢNG PHÂN QUYỀN VÀ CẬP NHẬT BẢNG USERS
-- ------------------------------------------------------------------------------

-- 1.1 Tạo bảng Roles
CREATE TABLE roles (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name         VARCHAR(100) NOT NULL UNIQUE,
    description  TEXT,
    is_active    BOOLEAN NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP DEFAULT now()
);

-- 1.2 Tạo bảng Permissions
CREATE TABLE permissions (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    resource     VARCHAR(100) NOT NULL, -- vd: invoice, payment, user
    action       VARCHAR(50) NOT NULL,  -- vd: create, read, update, delete
    description  TEXT,
    created_at   TIMESTAMP DEFAULT now()
);

-- 1.3 Tạo bảng Role_Permissions
CREATE TABLE role_permissions (
    role_id       UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    created_at    TIMESTAMP DEFAULT now(),
    PRIMARY KEY (role_id, permission_id)
);

-- 1.4 Cập nhật bảng Users
ALTER TABLE users ADD COLUMN role_id UUID;

-- (MIGRATION DỮ LIỆU CŨ TÙY CHỌN): 
-- Tự động lấy các role string cũ đang có để tạo role mới và map sang user
INSERT INTO roles (name, description)
SELECT DISTINCT role, 'Migrated from v3 role string' FROM users WHERE role IS NOT NULL;

UPDATE users SET role_id = (SELECT id FROM roles WHERE roles.name = users.role);
-- ---

-- 1.5 Thiết lập ràng buộc và xóa cột cũ
ALTER TABLE users ADD CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id);
ALTER TABLE users DROP COLUMN role; -- Cẩn thận: Đảm bảo đã chạy migration data trước khi drop

ALTER TABLE public.invoices
    ADD COLUMN approval_status VARCHAR(20) DEFAULT NULL,       -- pending | approved | rejected
    ADD COLUMN approval_comment TEXT DEFAULT NULL,             -- lý do từ chối
    ADD COLUMN camunda_process_id VARCHAR(255) DEFAULT NULL;   -- lưu processInstanceKey từ Camunda



-- ------------------------------------------------------------------------------
-- PHẦN 2: XÓA CÁC RÀNG BUỘC KHÓA NGOẠI (FOREIGN KEYS) CỦA CORE ĐỐI VỚI IDENTITY
-- (Lưu ý: PostgreSQL tự động đặt tên cho FK constraint dưới dạng [table]_[column]_fkey)
-- ------------------------------------------------------------------------------

ALTER TABLE fiscal_periods 
    DROP CONSTRAINT IF EXISTS fiscal_periods_company_id_fkey,
    DROP CONSTRAINT IF EXISTS fiscal_periods_closed_by_fkey;

ALTER TABLE accounts 
    DROP CONSTRAINT IF EXISTS accounts_company_id_fkey;

ALTER TABLE partners 
    DROP CONSTRAINT IF EXISTS partners_company_id_fkey;

ALTER TABLE journal_entries 
    DROP CONSTRAINT IF EXISTS journal_entries_company_id_fkey,
    DROP CONSTRAINT IF EXISTS journal_entries_created_by_fkey,
    DROP CONSTRAINT IF EXISTS journal_entries_posted_by_fkey;

ALTER TABLE invoices 
    DROP CONSTRAINT IF EXISTS invoices_company_id_fkey,
    DROP CONSTRAINT IF EXISTS invoices_created_by_fkey;

ALTER TABLE payments 
    DROP CONSTRAINT IF EXISTS payments_company_id_fkey,
    DROP CONSTRAINT IF EXISTS payments_created_by_fkey;

ALTER TABLE bank_accounts 
    DROP CONSTRAINT IF EXISTS bank_accounts_company_id_fkey;

ALTER TABLE audit_logs 
    DROP CONSTRAINT IF EXISTS audit_logs_changed_by_fkey;

-- ------------------------------------------------------------------------------
-- KẾT QUẢ: 
-- Hiện tại bảng Core chỉ còn lưu kiểu dữ liệu UUID cho các cột company_id và user_id, 
-- không còn bị rằng buộc dính liền với bảng users/companies nữa.
-- ==============================================================================
