---
name: ap_bill_approval_management
description: Hướng dẫn quản lý quy trình phê duyệt AP Bill (Hoá đơn phải trả) tích hợp Camunda 8 trong hệ thống EFMS.
---

# Quản lý Quy trình Phê duyệt AP Bill (Camunda 8)

Tài liệu này cung cấp hướng dẫn cho Agent để hỗ trợ người dùng (Accountant/Manager) trong việc vận hành quy trình AP Bill.

## 1. Kiểm tra Trạng thái Hoá đơn
Trước khi thực hiện bất kỳ hành động nào, hãy kiểm tra trạng thái hiện tại của hoá đơn trong `Invoice` entity:
- **`status`**: `draft`, `open`, `paid`, ...
- **`approval_status`**: `pending`, `approved`, `rejected`.
- **`camunda_process_id`**: Để liên kết với Camunda Operate/Tasklist.

## 2. Các Bước Vận hành Chính

### A. Xác nhận Hoá đơn (Confirm)
Khi Accountant yêu cầu xác nhận hoá đơn (Confirm):
1. Đảm bảo hoá đơn đang ở trạng thái `draft`.
2. Gọi `InvoiceService.confirm(id)`.
3. Kiểm tra xem `ZeebeClient` có khởi tạo process `ap-bill-approval` thành công không.
4. Trả về thông tin `camunda_process_id` cho người dùng nếu cần.

### B. Phê duyệt Hoá đơn (Approval/Rejection)
Nếu đóng vai trò hỗ trợ Finance Manager:
1. Sử dụng **Tasklist API v1** (`/v1/tasks/search`) để tìm ID của task (ở trạng thái `CREATED`) tương ứng với `camunda_process_id`.
2. Sử dụng **Zeebe REST API v2** (`/v2/user-tasks/{taskId}/completion`) để complete Zeebe User Task, truyền vào các biến dưới dạng flat JSON:
   - `approved`: `true` hoặc `false`.
   - `comment`: Lý do phê duyệt hoặc từ chối.
3. Nếu Approve: Kiểm tra xem Job Worker `create-journal-entry` có hoàn thành việc tạo bút toán không.
4. Nếu Reject: Đảm bảo `approval_status` chuyển thành `rejected` và Accountant nhận được thông báo.

## 3. Xử lý Lỗi & Troubleshooting
- **Lỗi Connection Camunda:** Kiểm tra thông tin xác thực trong `application-dev.yaml` (clientId, clusterId).
- **Job Worker không chạy:** Đảm bảo các class có annotation `@JobWorker` đang hoạt động và connected tới Zeebe.
- **Dữ liệu không đồng bộ:** Nếu `approval_status` là `approved` nhưng chưa có `JournalEntry`, hãy kiểm tra log của `CreateJournalEntryWorker`.

## 4. Ràng buộc quan trọng
- **Ngưỡng 100M:** Khi hỗ trợ thiết kế hoặc debug BPMN, luôn nhớ quy tắc rẽ nhánh dựa trên `totalAmount > 100000000`.
- **Thanh toán:** Không được hướng dẫn hoặc thực hiện thanh toán nếu `approval_status` != `approved`.
