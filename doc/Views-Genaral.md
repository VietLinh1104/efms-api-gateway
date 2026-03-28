# Tổng quan

EFMS gồm **6 module chính**, tổng **19 màn hình**.

| # | Module | Screens |
| --- | --- | --- |
| --- | -------- | :-------: |
| 1 | 🏠 Dashboard — Trang tổng quan | 1 |
| 2 | 📒 General Ledger — Sổ cái kế toán | 4 |
| 3 | 📋 Invoices — Hóa đơn (AR + AP gộp) | 6 |
| 4 | 🏦 Cash & Bank — Tiền mặt & Ngân hàng | 4 |
| 5 | 📊 Reports — Báo cáo tài chính | 4 |
| 6 | ⚙️ Settings — Cài đặt hệ thống | 3 |

---

# 1. 🏠 Dashboard — Trang tổng quan `/dashboard`

Không có workflow — đọc dữ liệu tổng hợp real-time.

- **KPI Cards**: Revenue (Doanh thu), Expense (Chi phí), Net Profit (Lợi nhuận), Cash Balance (Tồn quỹ), Receivable (Phải thu), Payable (Phải trả)
- **Charts**: Revenue vs Expense (bar), Cash Flow (line), AR Aging (pie)
- **Tables**: Invoices sắp đến hạn, Bills chờ thanh toán, Journal Entries gần nhất

---

# 2. 📒 General Ledger — Sổ cái kế toán

**Workflow:**

```
Tạo Chart of Accounts → Tạo Journal Entry (draft) → Kiểm tra Debit = Credit → Post → Xem Trial Balance → Đóng kỳ
```

| Loại | Màn hình EN | Màn hình VI | Path |
| --- | --- | --- | --- |
| Listing | Chart of Accounts | Danh mục tài khoản | `/accounting/accounts` |
| Form | Account Form | Tạo / Sửa tài khoản | `/accounting/accounts/new` |
| Listing | Journal Entries | Danh sách chứng từ | `/accounting/journals` |
| Form | Journal Entry Form | Tạo chứng từ kế toán | `/accounting/journals/new` |

---

# 3. 📋 Invoices — Hóa đơn AR + AP (gộp)

AR Invoice và AP Bill dùng chung 1 bộ màn hình, phân biệt bằng `type = ar / ap`.

**Workflow AR (Phải thu):**

```
Tạo Partner → Tạo Invoice (draft) → Confirm (open) → Nhận Payment → Allocate → Paid
```

**Workflow AP (Phải trả):**

```
Tạo Partner → Tạo Bill (draft) → Confirm → Approve → Chi Payment → Allocate → Paid
```

| Loại | Màn hình EN | Màn hình VI | Path |
| --- | --- | --- | --- |
| Listing | Partners | Danh sách đối tác (KH + NCC) | `/partners` |
| Form | Partner Form | Tạo / Sửa đối tác | `/partners/new` |
| Listing | Invoices / Bills | Danh sách hóa đơn / chứng từ mua | `/invoices` |
| Form | Invoice / Bill Form | Tạo hóa đơn / chứng từ mua | `/invoices/new` |
| Listing | Payments | Danh sách thanh toán | `/payments` |
| Form | Payment Form | Tạo phiếu thu / chi | `/payments/new` |

> **Bỏ**: Aging report riêng → thêm cột Overdue vào listing Invoices
> 

> **Bỏ**: Approval form riêng → Approve/Reject inline trong Invoice detail
> 

---

# 4. 🏦 Cash & Bank — Tiền mặt & Ngân hàng

**Workflow:**

```
Thiết lập Bank Account → Ghi giao dịch → Import sao kê → Đối chiếu (Reconciliation) → Báo cáo tồn quỹ
```

| Loại | Màn hình EN | Màn hình VI | Path |
| --- | --- | --- | --- |
| Listing | Bank Accounts | Danh sách tài khoản ngân hàng | `/finance/accounts` |
| Form | Bank Account Form | Tạo tài khoản ngân hàng | `/finance/accounts/new` |
| Listing | Transactions | Danh sách giao dịch | `/finance/transactions` |
| Listing | Reconciliation | Đối chiếu ngân hàng | `/finance/reconciliation` |

---

# 5. 📊 Reports — Báo cáo tài chính

Chọn **Company + Kỳ báo cáo** → Tổng hợp từ journal_lines → Export PDF/Excel.

| Màn hình EN | Màn hình VI | Path |
| --- | --- | --- |
| Balance Sheet | Bảng cân đối kế toán | `/reports/balance-sheet` |
| Profit & Loss | Kết quả kinh doanh | `/reports/profit-loss` |
| Cash Flow | Lưu chuyển tiền tệ | `/reports/cash-flow` |
| AR/AP Aging | Báo cáo tuổi nợ | `/reports/aging` |

---

# 6. ⚙️ Settings — Cài đặt hệ thống

| Loại | Màn hình EN | Màn hình VI | Path |
| --- | --- | --- | --- |
| Form | Company | Thông tin công ty | `/settings/company` |
| Listing + Form | Users & Roles | Người dùng & Phân quyền | `/settings/users` |
| Listing + Form | Fiscal Periods | Kỳ kế toán | `/settings/periods` |

---

# Phân quyền RBAC — Tóm tắt

| Chức năng | Admin | Finance Mgr | Accountant | Auditor |
| --- | --- | --- | --- | --- |
| ----------- | :-----: | :-----------: | :----------: | :-------: |
| Chart of Accounts | ✅ | ✅ | 👁️ | 👁️ |
| Tạo Journal Entry | ✅ | ✅ | ✅ | ❌ |
| Post Journal Entry | ✅ | ✅ | ❌ | ❌ |
| Invoice / Payment | ✅ | ✅ | ✅ | ❌ |
| Approve Bill | ✅ | ✅ | ❌ | ❌ |
| Reports | ✅ | ✅ | ✅ | ✅ |
| Settings | ✅ | ❌ | ❌ | ❌ |