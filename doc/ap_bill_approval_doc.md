# Quy trình Nghiệp vụ: Tạo & Duyệt & Chi tiền AP Bill (Camunda 8)

Tài liệu này mô tả quy trình luồng công việc cho các hoá đơn phải trả (AP Bill) trong hệ thống EFMS, tích hợp với Camunda 8 để quản lý quy trình phê duyệt.

## 1. Tổng quan Quy trình (High-Level Flow)

Quy trình bắt đầu khi một Accountant tạo một AP Bill mới và xác nhận nó. Sau đó, một quy trình phê duyệt trên Camunda 8 (`ap-bill-approval`) sẽ được khởi tạo.

1.  **Khởi tạo:** Accountant tạo Invoice (status: `draft`).
2.  **Xác nhận:** Accountant nhấn "Confirm" -> status chuyển sang `open`, `approval_status` chuyển sang `pending`.
3.  **Điều phối (Orchestration):** Hệ thống khởi động process `ap-bill-approval` trên Camunda 8.
4.  **Phê duyệt:** Finance Manager (và Admin nếu cần) kiểm tra và quyết định Approve hoặc Reject.
5.  **Hậu phê duyệt (Approved):**
    *   `approval_status` -> `approved`.
    *   Hệ thống tự động tạo Journal Entry (Bút toán) thông qua Job Worker.
    *   Accountant thực hiện thanh toán (Payment) -> status `paid`.
6.  **Hậu từ chối (Rejected):**
    *   `approval_status` -> `rejected`.
    *   Accountant nhận thông báo để chỉnh sửa lại hoá đơn.

---

## 2. Các Trạng thái (Statuses)

### Trạng thái Hoá đơn (Invoice Status)
| Trạng thái | Mô tả |
| :--- | :--- |
| `draft` | Hoá đơn mới tạo, đang chờ chỉnh sửa. |
| `open` | Hoá đơn đã xác nhận, đang trong quy trình duyệt hoặc chờ thanh toán. |
| `partial` | Hoá đơn đã thanh toán một phần. |
| `paid` | Hoá đơn đã thanh toán đầy đủ. |
| `cancelled` | Hoá đơn bị huỷ bỏ. |

### Trạng thái Phê duyệt (Approval Status)
| Trạng thái | Mô tả |
| :--- | :--- |
| `pending` | Đang chờ cấp quản lý phê duyệt. |
| `approved` | Đã được phê duyệt chính thức. |
| `rejected` | Bị từ chối phê duyệt. |

---

## 3. Camunda 8 Process: `ap-bill-approval`

### Các Key Task & Service
*   **Start Event:** Khi hoá đơn được confirm.
*   **User Task (Finance Manager review):** Duyệt hoá đơn.
*   **Exclusive Gateway:** Kiểm tra số tiền (> 100 triệu VND cần thêm Admin duyệt).
*   **Service Task (Job Worker):** `create-journal-entry` - Tự động tạo bút toán khi Approved.
*   **Service Task (Job Worker):** `notify-rejection` - Xử lý khi bị Reject.

### Variables truyền vào Process
*   `invoiceId`: UUID của hoá đơn.
*   `totalAmount`: Tổng tiền hoá đơn (dùng để rẽ nhánh gateway).
*   `companyId`: ID công ty.

---

## 4. Ràng buộc & Quy tắc (Business Rules)
*   **Thanh toán:** Chỉ được phép tạo Payment cho hoá đơn khi `approval_status` = `approved`.
*   **Ngưỡng phê duyệt:** Hoá đơn có `totalAmount` > 100,000,000 VND bắt buộc phải qua bước duyệt của Admin sau khi Finance Manager đã duyệt.
*   **Sửa đổi:** Hoá đơn ở trạng thái `pending` hoặc `approved` không được phép sửa đổi thông tin tài chính trừ khi bị `rejected`.

---

## 5. Tích hợp Kỹ thuật
*   **Zeebe Client:** Dùng để start process instance.
*   **Tasklist API:** Dùng để fetch và complete các User Task từ giao diện EFMS.
*   **Job Workers:** Các worker chạy ngầm trong `efms-core-service` để thực hiện logic nghiệp vụ sau các bước duyệt.
