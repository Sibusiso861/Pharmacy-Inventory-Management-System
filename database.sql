CREATE DATABASE IF NOT EXISTS pims_db;
USE pims_db;

-- Table 1: users
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('Admin', 'Cashier') NOT NULL,
    full_name VARCHAR(100) NOT NULL
    );

-- Table 2: suppliers
CREATE TABLE IF NOT EXISTS suppliers (
    supplier_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT
    );

-- Table 3: medicines
CREATE TABLE IF NOT EXISTS medicines (
    medicine_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    company VARCHAR(100),
    medicine_type VARCHAR(50),
    price DECIMAL(10,2) NOT NULL,
    quantity_in_stock INT NOT NULL,
    reorder_level INT NOT NULL,
    expiry_date DATE NOT NULL,
    supplier_id INT,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id) ON DELETE SET NULL
    );

-- Table 4: sales
CREATE TABLE IF NOT EXISTS sales (
    sale_id INT AUTO_INCREMENT PRIMARY KEY,
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
    );

-- Table 5: sale_items
CREATE TABLE IF NOT EXISTS sale_items (
            sale_item_id INT AUTO_INCREMENT PRIMARY KEY,
            sale_id INT,
            medicine_id INT,
            quantity_sold INT NOT NULL,
            price_at_sale DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (sale_id) REFERENCES sales(sale_id) ON DELETE CASCADE,
    FOREIGN KEY (medicine_id) REFERENCES medicines(medicine_id)
    );

  --Inserting Sample Data

INSERT INTO users (username, password, role, full_name) VALUES
     ('admin', 'admin123', 'Admin', 'System Administrator'),
     ('cashier', 'cash123', 'Cashier', 'John Cashier');

INSERT INTO suppliers (name, contact_person, phone, email, address) VALUES
     ('PharmaCorp SA', 'Jane Doe', '0112345678', 'info@pharmacorp.co.za', '12 Health Street, Johannesburg'),
     ('MedSupplies Ltd', 'Mark Smith', '0319876543', 'sales@medsupplies.co.za', '45 Cure Avenue, Durban');

INSERT INTO medicines (name, company, medicine_type, price, quantity_in_stock, reorder_level, expiry_date, supplier_id) VALUES
     ('Paracetamol 500mg', 'PharmaCorp SA', 'Tablet', 45.50, 150, 20, '2027-12-31', 1),
     ('Amoxicillin 250mg', 'MedSupplies Ltd', 'Capsule', 120.00, 8, 15, '2026-11-15', 2),
     ('Cough Syrup Expectorant', 'PharmaCorp SA', 'Syrup', 85.00, 40, 10, '2026-10-28', 1);


public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/pims_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root"; // Update with your MySQL username
    private static final String PASSWORD = ""; // Update with your MySQL password

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
} catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
}
        return conn;
}
}