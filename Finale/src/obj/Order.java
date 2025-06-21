package obj;

import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Order {
    private IntegerProperty orderId;
    private IntegerProperty customerId;
    private IntegerProperty employeeId;
    private ObjectProperty<LocalDateTime> date;
    private StringProperty status;
    private StringProperty paymentMethod;
    private StringProperty paymentStatus;
    private StringProperty note;
    private StringProperty deliveryAddress;
    private LongProperty total;
    
    // Thêm properties cho display
    private StringProperty customerName;

    public Order() {
        this.orderId = new SimpleIntegerProperty();
        this.customerId = new SimpleIntegerProperty();
        this.employeeId = new SimpleIntegerProperty();
        this.date = new SimpleObjectProperty<>();
        this.status = new SimpleStringProperty();
        this.paymentMethod = new SimpleStringProperty();
        this.paymentStatus = new SimpleStringProperty();
        this.note = new SimpleStringProperty();
        this.deliveryAddress = new SimpleStringProperty();
        this.total = new SimpleLongProperty();
        this.customerName = new SimpleStringProperty();
    }

    public Order(int orderId, int customerId, int employeeId, LocalDateTime date, 
                String status, String paymentMethod, String paymentStatus, String note, 
                String deliveryAddress, long total) {
        this();
        setOrderId(orderId);
        setCustomerId(customerId);
        setEmployeeId(employeeId);
        setDate(date);
        setStatus(status);
        setPaymentMethod(paymentMethod);
        setPaymentStatus(paymentStatus);
        setNote(note);
        setDeliveryAddress(deliveryAddress);
        setTotal(total);
    }

    // Getters
    public int getOrderId() { return orderId.get(); }
    public int getCustomerId() { return customerId.get(); }
    public int getEmployeeId() { return employeeId.get(); }
    public LocalDateTime getDate() { return date.get(); }
    public String getStatus() { return status.get(); }
    public String getPaymentMethod() { return paymentMethod.get(); }
    public String getPaymentStatus() { return paymentStatus.get(); }
    public String getNote() { return note.get(); }
    public String getDeliveryAddress() { return deliveryAddress.get(); }
    public long getTotal() { return total.get(); }
    public String getCustomerName() { return customerName.get(); }

    // Setters
    public void setOrderId(int value) { this.orderId.set(value); }
    public void setCustomerId(int value) { this.customerId.set(value); }
    public void setEmployeeId(int value) { this.employeeId.set(value); }
    public void setDate(LocalDateTime value) { this.date.set(value); }
    public void setStatus(String value) { this.status.set(value); }
    public void setPaymentMethod(String value) { this.paymentMethod.set(value); }
    public void setPaymentStatus(String value) { this.paymentStatus.set(value); }
    public void setNote(String value) { this.note.set(value); }
    public void setDeliveryAddress(String value) { this.deliveryAddress.set(value); }
    public void setTotal(long value) { this.total.set(value); }
    public void setCustomerName(String value) { this.customerName.set(value); }

    // Properties for JavaFX binding
    public IntegerProperty orderIdProperty() { return orderId; }
    public IntegerProperty customerIdProperty() { return customerId; }
    public IntegerProperty employeeIdProperty() { return employeeId; }
    public ObjectProperty<LocalDateTime> dateProperty() { return date; }
    public StringProperty statusProperty() { return status; }
    public StringProperty paymentMethodProperty() { return paymentMethod; }
    public StringProperty paymentStatusProperty() { return paymentStatus; }
    public StringProperty noteProperty() { return note; }
    public StringProperty deliveryAddressProperty() { return deliveryAddress; }
    public LongProperty totalProperty() { return total; }
    public StringProperty customerNameProperty() { return customerName; }

    // Formatted getters for display
    public String getFormattedDate() {
        return date.get() != null ? date.get().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
    }

    public String getFormattedTotal() {
        return String.format("%,d VND", total.get());
    }

    public String getStatusDisplay() {
        switch (status.get()) {
            case "offline": return "Offline";
            case "pending": return "Chờ xác nhận";
            case "confirmed": return "Đã xác nhận";
            case "cancelled": return "Đã hủy";
            default: return status.get();
        }
    }

    public String getPaymentMethodDisplay() {
        switch (paymentMethod.get()) {
            case "cash": return "Tiền mặt";
            case "bank": return "Chuyển khoản";
            default: return paymentMethod.get();
        }
    }

    public String getPaymentStatusDisplay() {
        switch (paymentStatus.get()) {
            case "paid": return "Đã thanh toán";
            case "unpaid": return "Chưa thanh toán";
            case "pending": return "Đang xử lý";
            default: return paymentStatus.get();
        }
    }
}