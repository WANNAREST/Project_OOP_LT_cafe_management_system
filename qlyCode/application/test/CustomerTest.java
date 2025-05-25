package application.test;

import application.Customer;

public class CustomerTest {
    public static void main(String[] args) {
        // Tạo một khách hàng mới (mật khẩu mặc định là 1234)
        Customer customer = new Customer(
                "0987654321",
                "Tran Thi B",
                null,
                "1234", // mật khẩu mặc định
                "45 Nguyen Van Cu, Da Nang"
        );

        System.out.println("== Thông tin khách hàng mới tạo ==");
        customer.printAccountInfo();

        // Đổi mật khẩu sau lần đăng nhập đầu tiên
        boolean changed = customer.changePassword("1234", "newpass2025");
        if (changed) {
            System.out.println("Đổi mật khẩu thành công.");
        } else {
            System.out.println("Đổi mật khẩu thất bại.");
        }

        // Kiểm tra trạng thái đăng nhập lần đầu
        System.out.println("First login? " + customer.isFirstLogin());

        // Cập nhật thông tin khách hàng
        customer.setEmail("tranb@example.com");
        customer.setAddress("Updated: 99 Hai Ba Trung, Da Nang");

        System.out.println("\n== Thông tin sau cập nhật ==");
        customer.printAccountInfo();
    }
}

