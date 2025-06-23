package obj;

import java.util.HashMap;
import java.util.Map;

public class CustomerManager {
    private static Map<String, Customer> customers = new HashMap<>();

    // Thêm khách hàng mới
    public static void addCustomer(Customer customer) {
        customers.put(customer.getPhoneNumber(), customer);
        System.out.println("Đã thêm thông tin chi tiết cho khách hàng: " + customer.getName());
    }

    // Tìm khách hàng bằng số điện thoại
    public static Customer getCustomerByPhone(String phoneNumber) {
        return customers.get(phoneNumber); // Trả về khách hàng từ Map
    }

    // Thêm đơn hàng vào lịch sử của khách hàng
    public static void addOrderToCustomer(Order order) {
    	
    	// Kiểm tra số điện thoại rỗng/null
        if (order.getPhoneNumber() == null || order.getPhoneNumber().trim().isEmpty()) {
            System.out.println("Không lưu lịch sử đơn hàng (số điện thoại trống).");
            return; // Dừng xử lý
        }
    	
        Customer customer = customers.get(order.getPhoneNumber());
        if (customer != null) {
            customer.addOrder(order);
        } else {
            // Nếu khách hàng chưa tồn tại, tạo mới
            Customer newCustomer = new Customer(order.getCustomerName(), 
                                             order.getPhoneNumber(), 
                                             order.getDeliveryAddress());
            newCustomer.addOrder(order);
            addCustomer(newCustomer);
        }
    }
}