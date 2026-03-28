# 1. 🏠 Dashboard — Trang tổng quan `/dashboard`

| Field | EN | VI | Ghi chú |
| --- | --- | --- | --- |
| Doanh thu | Revenue | Doanh thu | Tháng hiện tại |
| Chi phí | Expense | Chi phí | Tháng hiện tại |
| Lợi nhuận | Net Profit | Lợi nhuận ròng | Revenue - Expense |
| Tồn quỹ | Cash Balance | Số dư tiền mặt | Tổng cash + bank |
| Phải thu | Total Receivable | Tổng phải thu | Chưa thu |
| Phải trả | Total Payable | Tổng phải trả | Chưa trả |

---

# 2. 📒 General Ledger — Sổ cái kế toán

## 2.1 Listing — Danh mục tài khoản (Chart of Accounts) `/accounting/accounts`

| Field | EN | VI |
| --- | --- | --- |
| Mã tài khoản | Account Code | Mã TK |
| Tên tài khoản | Account Name | Tên tài khoản |
| Loại | Type | Loại (Asset / Liability / Equity / Revenue / Expense) |
| Số dư bình thường | Normal Balance | Dư Nợ / Dư Có |
| Tài khoản cha | Parent Account | Tài khoản cấp trên |
| Trạng thái | Status | Hoạt động / Ngừng |

## 2.2 Form — Tạo/Sửa tài khoản (Chart of Accounts) `/accounting/accounts/new`

| Field | EN | VI | Bắt buộc |
| --- | --- | --- | --- |
| Mã tài khoản | Account Code | Mã TK | ✅ |
| Tên tài khoản | Account Name | Tên tài khoản | ✅ |
| Loại tài khoản | Account Type | Loại | ✅ |
| Số dư bình thường | Balance Type | Debit / Credit | ✅ |
| Tài khoản cha | Parent Account | Tài khoản cấp trên | ❌ |
| Hoạt động | Is Active | Kích hoạt | ✅ |

---

## 2.3 Listing — Danh sách chứng từ (Journal Entries) `/accounting/journals`

| Field | EN | VI |
| --- | --- | --- |
| Số chứng từ | Entry No. | Số CT |
| Ngày | Date | Ngày chứng từ |
| Tham chiếu | Reference | Số tham chiếu |
| Mô tả | Description | Diễn giải |
| Tổng nợ | Total Debit | Tổng ghi nợ |
| Tổng có | Total Credit | Tổng ghi có |
| Trạng thái | Status | Draft / Posted / Cancelled |
| Người tạo | Created By | Người tạo |
| Ngày tạo | Created At | Ngày tạo |

## 2.4 Form — Tạo chứng từ (Journal Entry) `/accounting/journals/new`

**Header**

| Field | EN | VI | Bắt buộc |
| --- | --- | --- | --- |
| Ngày chứng từ | Entry Date | Ngày CT | ✅ |
| Số tham chiếu | Reference | Tham chiếu | ❌ |
| Diễn giải | Description | Mô tả | ❌ |
| Kỳ kế toán | Fiscal Period | Kỳ KT | ✅ |
| Nguồn | Source | Manual / POS / HRM / Inventory | ✅ |

**Line Items — Dòng bút toán**

| Field | EN | VI | Bắt buộc |
| --- | --- | --- | --- |
| Tài khoản | Account | Tài khoản | ✅ |
| Đối tác | Partner | Khách hàng / NCC | ❌ |
| Diễn giải | Description | Diễn giải dòng | ❌ |
| Nợ | Debit | Ghi nợ | ❌ |
| Có | Credit | Ghi có | ❌ |
| Ngoại tệ | Currency | Loại tiền | ❌ |
| Tỷ giá | Exchange Rate | Tỷ giá | ❌ |

**Footer**: Tổng Nợ | Tổng Có | Chênh lệch (= 0 mới Post được)

---

## 2.5 Listing — Bảng cân đối tài khoản (Trial Balance) `/accounting/trial-balance`

| Field | EN | VI |
| --- | --- | --- |
| Mã TK | Account Code | Mã tài khoản |
| Tên TK | Account Name | Tên tài khoản |
| Dư đầu kỳ Nợ | Opening Debit | Số dư đầu Nợ |
| Dư đầu kỳ Có | Opening Credit | Số dư đầu Có |
| Phát sinh Nợ | Period Debit | PS Nợ trong kỳ |
| Phát sinh Có | Period Credit | PS Có trong kỳ |
| Dư cuối kỳ Nợ | Closing Debit | Số dư cuối Nợ |
| Dư cuối kỳ Có | Closing Credit | Số dư cuối Có |

---

# 3. 📋 Invoices — Hóa đơn (AR + AP gộp)

## 3.1 Listing — Đối tác / Khách hàng / NCC (Partners) `/partners`

| Field | EN | VI |
| --- | --- | --- |
| Tên | Name | Tên đối tác |
| Loại | Type | Khách hàng / NCC / Cả hai |
| Mã số thuế | Tax Code | MST |
| Điện thoại | Phone | SĐT |
| Email | Email | Email |
| Tổng phải thu | Total Receivable | Dư nợ phải thu |
| Tổng phải trả | Total Payable | Dư nợ phải trả |
| Trạng thái | Status | Hoạt động / Ngừng |

## 3.2 Form — Tạo/Sửa đối tác (Partner) `/partners/new`

| Field | EN | VI | Bắt buộc |
| --- | --- | --- | --- |
| Tên | Name | Tên đối tác | ✅ |
| Loại | Type | Customer / Vendor / Both | ✅ |
| Mã số thuế | Tax Code | MST | ❌ |
| Điện thoại | Phone | SĐT | ❌ |
| Email | Email | Email | ❌ |
| Địa chỉ | Address | Địa chỉ | ❌ |
| TK kế toán AR | AR Account | TK phải thu | ❌ |
| TK kế toán AP | AP Account | TK phải trả | ❌ |
| Hoạt động | Is Active | Kích hoạt | ✅ |

---

## 3.3 Listing — Danh sách hóa đơn / chứng từ mua (Invoices / Bills) `/invoices`

| Field | EN | VI |
| --- | --- | --- |
| Số hóa đơn | Invoice No. | Số HĐ |
| Loại | Type | AR Invoice / AP Bill |
| Ngày HĐ | Invoice Date | Ngày hóa đơn |
| Hạn thanh toán | Due Date | Ngày đến hạn |
| Đối tác | Partner | Khách hàng / NCC |
| Tiền tệ | Currency | Loại tiền |
| Tổng tiền | Total Amount | Tổng cộng |
| Đã thanh toán | Paid Amount | Đã trả |
| Còn lại | Remaining | Còn nợ |
| Quá hạn | Overdue | Quá hạn (ngày) |
| Trạng thái | Status | Draft / Open / Partial / Paid / Cancelled |

## 3.4 Form — Tạo hóa đơn / chứng từ mua (Invoice / Bill) `/invoices/new`

**Header**

| Field | EN | VI | Bắt buộc |
| --- | --- | --- | --- |
| Loại | Type | AR Invoice / AP Bill | ✅ |
| Đối tác | Partner | Khách hàng / NCC | ✅ |
| Số hóa đơn | Invoice Number | Số HĐ | ❌ |
| Ngày hóa đơn | Invoice Date | Ngày HĐ | ✅ |
| Hạn thanh toán | Due Date | Ngày đến hạn | ❌ |
| Tiền tệ | Currency | Loại tiền | ✅ |
| Tỷ giá | Exchange Rate | Tỷ giá | ❌ |

**Line Items**

| Field | EN | VI | Bắt buộc |
| --- | --- | --- | --- |
| Mô tả | Description | Diễn giải | ✅ |
| Tài khoản | Account | TK doanh thu / chi phí | ✅ |
| Số lượng | Quantity | SL | ✅ |
| Đơn giá | Unit Price | Đơn giá | ✅ |
| Thuế suất | Tax Rate | % Thuế | ❌ |
| Tiền thuế | Tax Amount | Tiền thuế | ❌ |
| Thành tiền | Amount | Thành tiền | ✅ |

**Footer**

| Field | EN | VI |
| --- | --- | --- |
| Tổng trước thuế | Subtotal | Tiền trước thuế |
| Tổng thuế | Tax Total | Tổng thuế |
| Tổng cộng | Total Amount | Tổng thanh toán |
| Đã thanh toán | Paid Amount | Đã trả |
| Còn nợ | Amount Due | Còn phải trả |

---

## 3.5 Listing — Danh sách thanh toán (Payments) `/payments`

| Field | EN | VI |
| --- | --- | --- |
| Ngày | Payment Date | Ngày thanh toán |
| Loại | Type | Thu / Chi |
| Đối tác | Partner | Khách hàng / NCC |
| Số tiền | Amount | Số tiền |
| Tiền tệ | Currency | Loại tiền |
| Phương thức | Method | Tiền mặt / CK ngân hàng / Séc |
| Tham chiếu | Reference | Số tham chiếu |
| Hóa đơn liên kết | Invoice | HĐ đã áp dụng |
| Chứng từ KT | Journal Entry | Bút toán |

## 3.6 Form — Tạo phiếu thu / chi (Payment) `/payments/new`

| Field | EN | VI | Bắt buộc |
| --- | --- | --- | --- |
| Loại | Payment Type | Thu (Receive) / Chi (Pay) | ✅ |
| Đối tác | Partner | Khách hàng / NCC | ✅ |
| Ngày thanh toán | Payment Date | Ngày TT | ✅ |
| Số tiền | Amount | Số tiền | ✅ |
| Tiền tệ | Currency | Loại tiền | ✅ |
| Tỷ giá | Exchange Rate | Tỷ giá | ❌ |
| Phương thức | Payment Method | Cash / Bank Transfer / Check | ✅ |
| Tài khoản NH | Bank Account | TK ngân hàng | ❌ |
| Tham chiếu | Reference | Số tham chiếu | ❌ |
| Hóa đơn áp dụng | Apply to Invoices | Chọn HĐ + số tiền phân bổ | ❌ |

---

# 4. 🏦 Cash & Bank — Tiền mặt & Ngân hàng

## 4.1 Listing — Danh sách tài khoản ngân hàng (Bank Accounts) `/finance/accounts`

| Field | EN | VI |
| --- | --- | --- |
| Tên tài khoản | Account Name | Tên TK |
| Ngân hàng | Bank | Tên ngân hàng |
| Số tài khoản | Account Number | STK |
| Loại | Type | Tiền mặt / Ngân hàng |
| Tiền tệ | Currency | Loại tiền |
| Số dư hiện tại | Current Balance | Số dư |
| TK kế toán | GL Account | TK kế toán liên kết |

## 4.2 Form — Tạo tài khoản ngân hàng (Bank Account) `/finance/accounts/new`

| Field | EN | VI | Bắt buộc |
| --- | --- | --- | --- |
| Tên TK | Account Name | Tên tài khoản | ✅ |
| Ngân hàng | Bank Name | Tên ngân hàng | ❌ |
| Số TK | Account Number | Số tài khoản | ❌ |
| Loại | Type | Cash / Bank | ✅ |
| Tiền tệ | Currency | Loại tiền | ✅ |
| Số dư đầu kỳ | Opening Balance | Số dư ban đầu | ❌ |
| TK kế toán | GL Account | Tài khoản kế toán | ✅ |

---

## 4.3 Listing — Danh sách giao dịch (Transactions) `/finance/transactions`

| Field | EN | VI |
| --- | --- | --- |
| Ngày | Date | Ngày giao dịch |
| Mô tả | Description | Diễn giải |
| Loại | Type | Thu vào / Chi ra |
| Số tiền | Amount | Số tiền |
| TK ngân hàng | Bank Account | Tài khoản |
| Tham chiếu | Reference | Số tham chiếu |
| Đã đối chiếu | Reconciled | Đã đối chiếu |

---

## 4.4 Listing — Đối chiếu ngân hàng (Reconciliation) `/finance/reconciliation`

| Field | EN | VI |
| --- | --- | --- |
| Ngày sao kê | Statement Date | Ngày sao kê |
| Mô tả | Description | Diễn giải |
| Số tiền sao kê | Statement Amount | Số tiền NH |
| Số tiền hệ thống | System Amount | Số tiền HT |
| Trạng thái | Status | Khớp / Chưa khớp |
| Chứng từ KT | Journal Entry | Bút toán liên kết |

**Footer**: Số dư sao kê | Số dư hệ thống | Chênh lệch

---

# 5. 📊 Reports — Báo cáo tài chính

Tất cả báo cáo có chung **Filter bar — Bộ lọc**:

| Field | EN | VI |
| --- | --- | --- |
| Công ty | Company | Công ty |
| Từ ngày | From Date | Từ ngày |
| Đến ngày | To Date | Đến ngày |
| Kỳ kế toán | Fiscal Period | Kỳ KT |
| Xuất file | Export | PDF / Excel |

### 5.1 Balance Sheet — Bảng cân đối kế toán

| Cột | EN | VI |
| --- | --- | --- |
| Tài khoản | Account | Tài khoản |
| Số tiền | Amount | Số tiền |
| Kỳ trước | Prior Period | Kỳ trước |
| Chênh lệch | Variance | Chênh lệch |

### 5.2 Profit & Loss — Kết quả kinh doanh

| Cột | EN | VI |
| --- | --- | --- |
| Chỉ tiêu | Item | Khoản mục |
| Kỳ này | Current Period | Kỳ này |
| Kỳ trước | Prior Period | Kỳ trước |
| % Thay đổi | % Change | % Biến động |

### 5.3 AR/AP Aging — Báo cáo tuổi nợ

| Cột | EN | VI |
| --- | --- | --- |
| Đối tác | Partner | Khách hàng / NCC |
| Hiện tại | Current | Chưa đến hạn |
| 1–30 ngày | 1–30 Days | Quá hạn 1–30 ngày |
| 31–60 ngày | 31–60 Days | Quá hạn 31–60 ngày |
| 61–90 ngày | 61–90 Days | Quá hạn 61–90 ngày |
| > 90 ngày | Over 90 Days | Quá hạn >90 ngày |
| Tổng | Total | Tổng cộng |

---

# 6. ⚙️ Settings — Cài đặt hệ thống

## 6.1 Form — Thông tin công ty (Company) `/settings/company`

| Field | EN | VI | Bắt buộc |
| --- | --- | --- | --- |
| Tên công ty | Company Name | Tên công ty | ✅ |
| Mã số thuế | Tax Code | MST | ❌ |
| Địa chỉ | Address | Địa chỉ | ❌ |
| Tiền tệ mặc định | Default Currency | Loại tiền chính | ✅ |
| Logo | Logo | Logo | ❌ |

## 6.2 Listing — Danh sách người dùng (Users) `/settings/users`

| Field | EN | VI |
| --- | --- | --- |
| Họ tên | Full Name | Họ tên |
| Email | Email | Email |
| Vai trò | Role | Vai trò |
| Trạng thái | Status | Hoạt động / Khóa |
| Ngày tạo | Created At | Ngày tạo |

## 6.3 Form — Tạo/Sửa người dùng (User) `/settings/users/new`

| Field | EN | VI | Bắt buộc |
| --- | --- | --- | --- |
| Họ tên | Full Name | Họ tên | ✅ |
| Email | Email | Email | ✅ |
| Mật khẩu | Password | Mật khẩu | ✅ |
| Vai trò | Role | Admin / Finance Mgr / Accountant / Auditor | ✅ |
| Hoạt động | Is Active | Kích hoạt | ✅ |

## 6.4 Listing — Danh sách kỳ kế toán (Fiscal Periods) `/settings/periods`

| Field | EN | VI |
| --- | --- | --- |
| Tên kỳ | Period Name | Tên kỳ (VD: Jan 2025) |
| Từ ngày | Start Date | Từ ngày |
| Đến ngày | End Date | Đến ngày |
| Trạng thái | Status | Mở / Đóng |
| Người đóng | Closed By | Người đóng kỳ |
| Ngày đóng | Closed At | Ngày đóng |