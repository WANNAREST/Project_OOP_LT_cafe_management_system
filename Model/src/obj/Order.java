package obj;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class Order {
    private StringProperty orderId;
    private StringProperty customerId;
    private StringProperty employeeId;
    private ObjectProperty<LocalDateTime> date;
    private StringProperty status;
    private StringProperty paymentMethod;
    private StringProperty paymentStatus;
    private StringProperty note;
    private StringProperty deliveryAddress;
    private DoubleProperty total;
    private IntegerProperty bonusUsed;
    private DoubleProperty originalTotal; // ✅ THÊM PROPERTY NÀY

    public Order() {
        this.orderId = new SimpleStringProperty();
        this.customerId = new SimpleStringProperty();
        this.employeeId = new SimpleStringProperty();
        this.date = new SimpleObjectProperty<>();
        this.status = new SimpleStringProperty();
        this.paymentMethod = new SimpleStringProperty();
        this.paymentStatus = new SimpleStringProperty();
        this.note = new SimpleStringProperty();
        this.deliveryAddress = new SimpleStringProperty();
        this.total = new SimpleDoubleProperty();
        this.bonusUsed = new SimpleIntegerProperty(0);
        this.originalTotal = new SimpleDoubleProperty(0); // ✅ KHỞI TẠO
    }

    // ✅ SỬA CONSTRUCTOR - chỉ giữ constructor cũ để không conflict
    public Order(String orderId, String customerId, String employeeId, LocalDateTime date, 
                String status, String paymentMethod, String paymentStatus, String note, 
                String deliveryAddress, double total) {
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
        setBonusUsed(0); // Default
        setOriginalTotal(total); // Default = total
    }

    // Getters
    public String getOrderId() { return orderId.get(); }
    public String getCustomerId() { return customerId.get(); }
    public String getEmployeeId() { return employeeId.get(); }
    public LocalDateTime getDate() { return date.get(); }
    public String getStatus() { return status.get(); }
    public String getPaymentMethod() { return paymentMethod.get(); }
    public String getPaymentStatus() { return paymentStatus.get(); }
    public String getNote() { return note.get(); }
    public String getDeliveryAddress() { return deliveryAddress.get(); }
    public double getTotal() { return total.get(); }
    public int getBonusUsed() { return bonusUsed.get(); }
    public double getOriginalTotal() { return originalTotal.get(); } // ✅ THÊM GETTER

    // Setters
    public void setOrderId(String value) { orderId.set(value); }
    public void setCustomerId(String value) { customerId.set(value); }
    public void setEmployeeId(String value) { employeeId.set(value); }
    public void setDate(LocalDateTime value) { date.set(value); }
    public void setStatus(String value) { status.set(value); }
    public void setPaymentMethod(String value) { paymentMethod.set(value); }
    public void setPaymentStatus(String value) { paymentStatus.set(value); }
    public void setNote(String value) { note.set(value); }
    public void setDeliveryAddress(String value) { deliveryAddress.set(value); }
    public void setTotal(double value) { total.set(value); }
    public void setBonusUsed(int value) { bonusUsed.set(value); }
    public void setOriginalTotal(double value) { originalTotal.set(value); } // ✅ THÊM SETTER

    // Properties
    public StringProperty orderIdProperty() { return orderId; }
    public StringProperty customerIdProperty() { return customerId; }
    public StringProperty employeeIdProperty() { return employeeId; }
    public ObjectProperty<LocalDateTime> dateProperty() { return date; }
    public StringProperty statusProperty() { return status; }
    public StringProperty paymentMethodProperty() { return paymentMethod; }
    public StringProperty paymentStatusProperty() { return paymentStatus; }
    public StringProperty noteProperty() { return note; }
    public StringProperty deliveryAddressProperty() { return deliveryAddress; }
    public DoubleProperty totalProperty() { return total; }
    public IntegerProperty bonusUsedProperty() { return bonusUsed; }
    public DoubleProperty originalTotalProperty() { return originalTotal; } // ✅ THÊM PROPERTY

    // Formatted methods
    public String getFormattedDate() {
        if (date.get() != null) {
            return date.get().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
        return "";
    }

    public String getFormattedTotal() {
        return String.format("%,.0f VND", total.get());
    }

    public String getFormattedOriginalTotal() { // ✅ THÊM METHOD
        return String.format("%,.0f VND", originalTotal.get());
    }

    // Customer name property
    private StringProperty customerName = new SimpleStringProperty("");
    public String getCustomerName() { return customerName.get(); }
    public void setCustomerName(String value) { customerName.set(value); }
    public StringProperty customerNameProperty() { return customerName; }

    // Bonus methods
    public String getBonusInfo() {
        if (bonusUsed.get() > 0) {
            return String.format("-%d điểm (-%,.0f VND)", bonusUsed.get(), bonusUsed.get() * 10.0);
        }
        return "Không sử dụng";
    }

    public double getDiscountAmount() {
        return bonusUsed.get() * 10.0;
    }

    public String getFormattedDiscount() {
        return String.format("%,.0f VND", getDiscountAmount());
    }
}