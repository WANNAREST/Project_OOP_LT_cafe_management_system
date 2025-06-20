-- =============================================
-- Cafe Shop Management System Database Setup
-- =============================================

-- Create Database
CREATE DATABASE IF NOT EXISTS cafestore;
USE cafestore;

-- =============================================
-- TABLE DEFINITIONS
-- =============================================

CREATE TABLE Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
	dob DATE,
    phone VARCHAR(20) UNIQUE,
    email VARCHAR(100) UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('customer', 'employee', 'manager') NOT NULL DEFAULT 'customer'
);

CREATE TABLE Customers (
	customer_id INT PRIMARY KEY,
    bonus_point INT DEFAULT 0 CHECK (bonus_point >= 0),
    FOREIGN KEY (customer_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

CREATE TABLE Employees (
	employee_id INT PRIMARY KEY,
	gender ENUM('male', 'female'),
    citizen_id VARCHAR(15) UNIQUE,
	FOREIGN KEY (employee_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

CREATE TABLE Products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    price INT NOT NULL CHECK (price >= 0),
    stock INT NOT NULL CHECK (stock >= 0),
    img_path TEXT,
    note VARCHAR(500)
);

CREATE TABLE Orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    employee_id INT,
    date DATETIME NOT NULL,
    status ENUM('offline', 'pending', 'confirmed', 'cancelled') NOT NULL DEFAULT 'pending',
    payment_method ENUM('cash', 'bank') NOT NULL,
    payment_status ENUM('paid', 'unpaid', 'pending') NOT NULL,
    note TEXT,
    delivery_address VARCHAR(255),
    total BIGINT NOT NULL CHECK (total >= 0),
    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id),
    FOREIGN KEY (employee_id) REFERENCES Employees(employee_id)
);

CREATE TABLE Orderlines (
    orderline_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    price INT NOT NULL CHECK (price >= 0),
    FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES Products(product_id) ON DELETE CASCADE  
);

CREATE TABLE Shifts (
    shift_id INT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    time ENUM('morning', 'afternoon', 'evening') NOT NULL
);

CREATE TABLE Shift_Details (
    shift_id INT,
    employee_id INT,
    status ENUM('scheduled', 'completed', 'absent', 'late') DEFAULT 'scheduled',
    PRIMARY KEY (shift_id, employee_id),
    FOREIGN KEY (shift_id) REFERENCES Shifts(shift_id) ON DELETE CASCADE,
    FOREIGN KEY (employee_id) REFERENCES Employees(employee_id) ON DELETE CASCADE
);

CREATE TABLE Salary (
    salary_id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT NOT NULL,
    month TINYINT NOT NULL CHECK (month BETWEEN 1 AND 12),
    year YEAR NOT NULL,
    working_days INT NOT NULL CHECK (working_days >= 0),
    absent_days INT NOT NULL CHECK (absent_days >= 0),
    base_salary INT NOT NULL DEFAULT 0 CHECK (base_salary >= 0),
    bonus INT DEFAULT 0 CHECK (bonus >= 0),
    total BIGINT DEFAULT 0 CHECK (total >= 0),
    FOREIGN KEY (employee_id) REFERENCES Employees(employee_id) ON DELETE CASCADE
);

-- =============================================
-- TEST DATA INSERTION
-- =============================================

-- Test Users
INSERT INTO Users (full_name, address, dob, phone, email, password, role) VALUES 
('Test Customer', '123 Customer Street, Ho Chi Minh City', '1995-05-15', '0987654321', 'customer@gmail.com', 'password123', 'customer'),
('Admin User', '456 Admin Avenue, Ho Chi Minh City', '1985-03-20', '0123456789', 'admin@gmail.com', 'admin123', 'manager'),
('Employee User', '789 Employee Road, Ho Chi Minh City', '1990-08-10', '0369852147', 'employee@gmail.com', 'employee123', 'employee'),
('Jane Customer', '321 Customer Lane, Ho Chi Minh City', '1992-12-25', '0912345678', 'jane@gmail.com', 'jane123', 'customer'),
('John Employee', '654 Staff Street, Ho Chi Minh City', '1988-07-30', '0987123456', 'john@gmail.com', 'john123', 'employee');

-- Customer Details (for users with role 'customer')
INSERT INTO Customers (customer_id, bonus_point) VALUES 
(1, 150),  -- Test Customer with 150 bonus points
(4, 75);   -- Jane Customer with 75 bonus points

-- Employee Details (for users with role 'employee' or 'manager')
INSERT INTO Employees (employee_id, gender, citizen_id) VALUES 
(2, 'male', '123456789012345'),    -- Admin User
(3, 'female', '987654321098765'),  -- Employee User  
(5, 'male', '456789123456789');    -- John Employee

-- Sample Products (Vietnamese Cafe Items)
INSERT INTO Products (product_name, category, price, stock, note) VALUES 
('Bạc xỉu', 'Cà phê', 27000, 60, 'Ngọt nhẹ, ít cà phê'),
('Cà phê đen nóng', 'Cà phê', 20000, 80, 'Rang đậm đặc biệt'),
('Cà phê sữa đá', 'Cà phê', 25000, 100, 'Phục vụ với đá'),
('Cacao nóng', 'Thức uống khác', 32000, 40, 'Đậm vị cacao'),
('Nước ép cam', 'Nước ép', 30000, 60, 'Không đường, tươi 100%'),
('Sinh tố bơ', 'Sinh tố', 40000, 50, 'Bơ sáp nguyên chất'),
('Soda chanh dây', 'Giải khát', 29000, 50, 'Mát lạnh, sủi bọt'),
('Trà đào cam sả', 'Trà', 30000, 70, 'Thanh mát, nhiều topping'),
('Trà sữa trân châu', 'Trà sữa', 35000, 90, 'Ngon, nhiều trân châu'),
('Yaourt đá', 'Món lạnh', 28000, 40, 'Chua ngọt vừa phải');

-- Sample Orders
INSERT INTO Orders (customer_id, employee_id, date, status, payment_method, payment_status, total) VALUES 
(1, 3, '2025-06-20 14:30:00', 'confirmed', 'cash', 'paid', 85000),
(4, 5, '2025-06-20 16:45:00', 'pending', 'bank', 'pending', 120000);

-- Sample Order Lines
INSERT INTO Orderlines (order_id, product_id, quantity, price) VALUES 
(1, 1, 2, 27000),  -- 2x Bạc xỉu
(1, 3, 1, 25000),  -- 1x Cà phê sữa đá
(1, 9, 1, 35000),  -- 1x Trà sữa trân châu
(2, 6, 2, 40000),  -- 2x Sinh tố bơ
(2, 8, 1, 30000),  -- 1x Trà đào cam sả
(2, 10, 1, 28000); -- 1x Yaourt đá

-- Sample Shifts
INSERT INTO Shifts (date, time) VALUES 
('2025-06-20', 'morning'),
('2025-06-20', 'afternoon'),
('2025-06-20', 'evening'),
('2025-06-21', 'morning'),
('2025-06-21', 'afternoon');

-- Sample Shift Details
INSERT INTO Shift_Details (shift_id, employee_id, status) VALUES 
(1, 3, 'completed'),
(1, 5, 'completed'),
(2, 2, 'completed'),
(2, 3, 'completed'),
(3, 5, 'scheduled'),
(4, 3, 'scheduled'),
(5, 2, 'scheduled');

-- Sample Salary Records
INSERT INTO Salary (employee_id, month, year, working_days, absent_days, base_salary, bonus, total) VALUES 
(2, 6, 2025, 22, 0, 15000000, 2000000, 17000000),  -- Admin User
(3, 6, 2025, 20, 2, 8000000, 500000, 8500000),     -- Employee User
(5, 6, 2025, 22, 0, 8500000, 1000000, 9500000);    -- John Employee

-- =============================================
-- VERIFICATION QUERIES
-- =============================================

-- Verify Users
SELECT 'USERS' as table_name;
SELECT user_id, full_name, role, phone, email FROM Users;

-- Verify Customers
SELECT 'CUSTOMERS' as table_name;
SELECT c.customer_id, u.full_name, c.bonus_point 
FROM Customers c 
JOIN Users u ON c.customer_id = u.user_id;

-- Verify Employees  
SELECT 'EMPLOYEES' as table_name;
SELECT e.employee_id, u.full_name, u.role, e.gender, e.citizen_id 
FROM Employees e 
JOIN Users u ON e.employee_id = u.user_id;

-- Verify Products
SELECT 'PRODUCTS' as table_name;
SELECT product_id, product_name, category, price, stock FROM Products LIMIT 5;

-- Test Login Credentials Summary
SELECT 'LOGIN CREDENTIALS' as info;
SELECT 
    'Customer Login:' as login_type,
    'Test Customer' as username, 
    'password123' as password,
    'customer' as role
UNION ALL
SELECT 
    'Admin Login:' as login_type,
    'Admin User' as username,
    'admin123' as password, 
    'manager' as role
UNION ALL
SELECT 
    'Employee Login:' as login_type,
    'Employee User' as username,
    'employee123' as password,
    'employee' as role; 