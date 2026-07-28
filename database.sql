CREATE DATABASE IF NOT EXISTS supplychain;
USE supplychain;

-- ------------------------------------------------------------
-- 1. Users Table (Authentication & Access Control)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role ENUM('admin', 'manager', 'staff') DEFAULT 'staff',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- 2. Suppliers Table
-- ------------------------------------------------------------
DROP TABLE IF EXISTS suppliers;
CREATE TABLE suppliers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    supplier_name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20),
    address VARCHAR(255),
    rating DECIMAL(3, 2) DEFAULT 5.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- 3. Products Table (Inventory Items)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS products;
CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    category VARCHAR(50) DEFAULT 'General',
    quantity INT NOT NULL DEFAULT 0,
    price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    min_stock_level INT DEFAULT 10,
    supplier_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE SET NULL
);

-- ------------------------------------------------------------
-- 4. Customer Orders Table
-- ------------------------------------------------------------
DROP TABLE IF EXISTS orders;
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    customer_name VARCHAR(100) NOT NULL,
    customer_email VARCHAR(100),
    shipping_address TEXT NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('Pending', 'Processing', 'Shipped', 'Delivered', 'Cancelled') DEFAULT 'Pending',
    total_amount DECIMAL(10, 2) DEFAULT 0.00
);

-- ------------------------------------------------------------
-- 5. Order Items Table
-- ------------------------------------------------------------
DROP TABLE IF EXISTS order_items;
CREATE TABLE order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- 6. Shipments & Route Tracking Table
-- ------------------------------------------------------------
DROP TABLE IF EXISTS shipments;
CREATE TABLE shipments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tracking_number VARCHAR(50) NOT NULL UNIQUE,
    order_id INT NOT NULL,
    carrier VARCHAR(100) NOT NULL,
    origin VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    current_location VARCHAR(100) NOT NULL,
    status ENUM('Preparing', 'In Transit', 'Out for Delivery', 'Delivered', 'Delayed') DEFAULT 'Preparing',
    estimated_delivery DATE,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- 7. Inventory Audit Logs Table
-- ------------------------------------------------------------
DROP TABLE IF EXISTS inventory_logs;
CREATE TABLE inventory_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    change_type ENUM('Restock', 'Order_Fulfilled', 'Manual_Adjustment') NOT NULL,
    quantity_change INT NOT NULL,
    notes VARCHAR(255),
    logged_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- ============================================================
-- Seed Data Insertion
-- ============================================================

-- Seed Users
INSERT INTO users (username, password, full_name, email, role) VALUES
('admin', 'admin123', 'System Administrator', 'admin@supplychain.com', 'admin'),
('manager', 'manager123', 'Logistics Manager', 'manager@supplychain.com', 'manager'),
('staff', 'staff123', 'Warehouse Operator', 'staff@supplychain.com', 'staff');

-- Seed Suppliers
INSERT INTO suppliers (supplier_name, contact_person, email, phone, address, rating) VALUES
('Global Freight & Logistics', 'Robert Vance', 'robert@globalfreight.com', '+1 555-0192', '100 Shipping Way, Chicago IL', 4.8),
('Apex Industrial Components', 'Elena Rostova', 'elena@apexcomponents.com', '+1 555-0482', '45 Industrial Pkwy, Houston TX', 4.6),
('PacNet Electronic Supplies', 'Kenji Sato', 'sato@pacnet.com', '+1 555-0923', '88 Tech Blvd, San Jose CA', 4.9);

-- Seed Products
INSERT INTO products (product_name, category, quantity, price, min_stock_level, supplier_id) VALUES
('Hydraulic Freight Lift Engine', 'Machinery', 15, 1250.00, 5, 2),
('Thermal Insulated Shipping Container', 'Packaging', 120, 85.50, 20, 1),
('GPS Cargo Tracker Unit', 'Electronics', 80, 149.99, 15, 3),
('Heavy-Duty Cargo Straps (Pack of 10)', 'Packaging', 300, 29.95, 50, 1),
('Barcode Handheld Scanner Terminal', 'Electronics', 25, 399.00, 8, 3);

-- Seed Orders
INSERT INTO orders (order_number, customer_name, customer_email, shipping_address, status, total_amount) VALUES
('ORD-2026-1001', 'Metro Retail Logistics', 'orders@metroretail.com', '500 Commerce St, Dallas TX', 'Shipped', 1635.45),
('ORD-2026-1002', 'Summit Distribution Inc', 'procurement@summitdist.com', '12 Regional Hub Way, Atlanta GA', 'Processing', 2500.00),
('ORD-2026-1003', 'Coastal Express Line', 'shipping@coastalexp.com', '77 Seaport Blvd, Seattle WA', 'Delivered', 749.95);

-- Seed Order Items
INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES
(1, 2, 10, 85.50),
(1, 3, 5, 149.99),
(2, 1, 2, 1250.00),
(3, 3, 5, 149.99);

-- Seed Shipments
INSERT INTO shipments (tracking_number, order_id, carrier, origin, destination, current_location, status, estimated_delivery) VALUES
('TRK-98214-US', 1, 'FedEx Freight', 'Chicago IL', 'Dallas TX', 'Memphis TN Hub', 'In Transit', '2026-07-30'),
('TRK-98215-US', 2, 'DHL Cargo', 'Houston TX', 'Atlanta GA', 'Houston TX Terminal', 'Preparing', '2026-08-02'),
('TRK-98216-US', 3, 'UPS Freight', 'San Jose CA', 'Seattle WA', 'Seattle WA Facility', 'Delivered', '2026-07-27');

-- Seed Inventory Logs
INSERT INTO inventory_logs (product_id, change_type, quantity_change, notes) VALUES
(1, 'Restock', 15, 'Initial stock import'),
(2, 'Restock', 150, 'Warehouse batch load'),
(2, 'Order_Fulfilled', -10, 'Fulfilling ORD-2026-1001'),
(3, 'Order_Fulfilled', -10, 'Fulfilling ORD-2026-1001 and ORD-2026-1003'
