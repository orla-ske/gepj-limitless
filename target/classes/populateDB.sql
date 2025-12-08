-- Insert sample data for Company
INSERT INTO company (city, street, post_address) VALUES
('New York', '123 Main St', 'PO Box 1001, NY 10001'),
('Los Angeles', '456 Oak Ave', 'PO Box 2002, LA 90001'),
('Chicago', '789 Elm Blvd', 'PO Box 3003, CHI 60601');

-- Insert sample data for User (passwords are SHA-256 encrypted)
INSERT INTO "user" (username, password, role, firstname, lastName, company_id) VALUES
('supplier1', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'SUPPLIER', 'John', 'Smith', 1),
('supplier2', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'SUPPLIER', 'Jane', 'Doe', 2),
('retailer1', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'RETAILER', 'Bob', 'Johnson', 3),
('retailer2', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'RETAILER', 'Alice', 'Williams', 1),
('admin', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'ADMIN', 'Admin', 'User', 1);

-- Insert sample data for Product
INSERT INTO product (name, price, stock, category, company_id) VALUES
('Laptop - 15 inch', 299.99, 50, 'ELECTRONICS', 1),
('T-Shirt - Size M', 49.99, 100, 'CLOTHING', 2),
('Organic Snacks', 19.99, 200, 'FOOD', 1),
('Office Chair', 599.99, 25, 'FURNITURE', 2),
('Notebook Set', 9.99, 150, 'OTHER', 1);

-- Insert sample data for Invoice
INSERT INTO invoice (source, destination) VALUES
('Supplier A Warehouse', 'Retailer 1 Store'),
('Supplier B Warehouse', 'Retailer 2 Store'),
('Supplier A Warehouse', 'Retailer 1 Store');

-- Insert sample data for Invoice_Product (linking invoices to products with quantities)
INSERT INTO invoice_product (invoice_id, product_id, quantity, price_at_purchase) VALUES
(1, 1, 10, 299.99),  -- Invoice 1: 10 Laptops
(2, 2, 50, 49.99),   -- Invoice 2: 50 T-Shirts
(3, 3, 100, 19.99);  -- Invoice 3: 100 Organic Snacks