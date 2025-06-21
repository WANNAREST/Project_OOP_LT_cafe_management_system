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
    FOREIGN KEY (employee_id) REFERENCES Employees(Employee_id) ON DELETE CASCADE
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

