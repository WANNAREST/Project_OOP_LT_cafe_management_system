package application;

import java.util.Calendar;
import java.util.Date;

public class Order {
    private int orderId;
    private int customerId;              // FK đến Customer
    private int employeeId;              // FK đến Employee (ai tạo đơn)
    private Date date;                   // ngày tạo đơn
    private OrderStatus status;          // trạng thái đơn hàng
    private PaymentMethod paymentMethod; // phương thức thanh toán
    private PaymentStatus paymentStatus; // trạng thái thanh toán
    private String note;                 // ghi chú đơn hàng
    private String deliveryAddress;      // địa chỉ giao hàng
    private double total;                // tổng tiền (bao gồm VAT)

    // Constructor tương ứng với DB schema
    public Order(int orderId, int customerId, int employeeId, Date date,
                 OrderStatus status, PaymentMethod paymentMethod,
                 PaymentStatus paymentStatus, String note,
                 String deliveryAddress, double total) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.employeeId = employeeId;
        this.date = date;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.note = note;
        this.deliveryAddress = deliveryAddress;
        this.total = total;
    }

    // Getter và Setter tương ứng với DB fields
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public void confirmPayment() {
        this.paymentStatus = PaymentStatus.COMPLETED;
    }

    public boolean isTakeaway() {
        return status == OrderStatus.TAKEAWAY;
    }

    public boolean isDineIn() {
        return status == OrderStatus.DINE_IN;
    }


    enum OrderStatus {

        TAKEAWAY("Mang về"),       // Tương ứng "MV" trong yêu cầu
        DINE_IN("Tại quán");       // Tương ứng "TQ" trong yêu cầu

        private final String description;

        OrderStatus(String description) {
            this.description = description;
        }

        public String getDescription() { return description; }
    }

    enum PaymentMethod {
        CASH("Tiền mặt"),
        BANK_TRANSFER("Chuyển khoản");

        private final String description;

        PaymentMethod(String description) {
            this.description = description;
        }

        public String getDescription() { return description; }
    }

    enum PaymentStatus {
        PENDING("Chờ thanh toán"),
        COMPLETED("Đã thanh toán");

        private final String description;

        PaymentStatus(String description) {
            this.description = description;
        }

        public String getDescription() { return description; }
    }
}
