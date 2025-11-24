-- Create Database
CREATE DATABASE IF NOT EXISTS inventory_management_system;
USE inventory_management_system;

-- Drop tables if they exist (in reverse order of dependencies)
DROP TABLE IF EXISTS invoice;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS company;

-- Create Company table
CREATE TABLE company (
    id INT PRIMARY KEY AUTO_INCREMENT,
    city VARCHAR(100),
    street VARCHAR(200),
    post_address VARCHAR(200)
);

-- Create User table with Role enum
CREATE TABLE user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('SUPPLIER', 'RETAILER') NOT NULL,
    firstname VARCHAR(255),
    lastName VARCHAR(255),
    company_id INT,
    FOREIGN KEY (company_id) REFERENCES company(id) ON DELETE SET NULL
);

-- Create Product table with Category enum
CREATE TABLE product (
    id INT PRIMARY KEY AUTO_INCREMENT,
    price DECIMAL(10, 2) NOT NULL,
    name VARCHAR(255),
    stock INT NOT NULL DEFAULT 0,
    category VARCHAR(255),
    company_id INT
    FOREIGN KEY (company_id) REFERENCES company(id) ON DELETE SET NULL
);

-- Create Invoice table
CREATE TABLE invoice (
    id INT PRIMARY KEY AUTO_INCREMENT,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    source VARCHAR(100),
    destination VARCHAR(100)
);

CREATE TABLE invoice_product (
    id INT PRIMARY KEY AUTO_INCREMENT,
    invoice_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    price_at_purchase DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (invoice_id) REFERENCES invoice(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    UNIQUE KEY unique_invoice_product (invoice_id, product_id),
    CHECK (quantity > 0)
);

-- Create indexes for better performance
CREATE INDEX idx_user_username ON user(username);
CREATE INDEX idx_user_company ON user(company_id);
CREATE INDEX idx_product_category ON product(category);
CREATE INDEX idx_invoice_date ON invoice(date);
CREATE INDEX idx_invoice_product_invoice ON invoice_product(invoice_id);
CREATE INDEX idx_invoice_product_product ON invoice_product(product_id);