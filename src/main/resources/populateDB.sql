INSERT INTO company (city, street, post_address) VALUES
('New York', '123 Main St', 'PO Box 1001, NY 10001'),
('Los Angeles', '456 Oak Ave', 'PO Box 2002, LA 90001'),
('Chicago', '789 Elm Blvd', 'PO Box 3003, CHI 60601');

-- Insert sample data for User
INSERT INTO user (username, password, role, company_id) VALUES
('supplier1', 'password123', 'SUPPLIER', 1),
('supplier2', 'password123', 'SUPPLIER', 2),
('retailer1', 'password123', 'RETAILER', 3),
('retailer2', 'password123', 'RETAILER', NULL);

-- Insert sample data for Product
INSERT INTO product (source, price, stock, category, attribute) VALUES
('Supplier A', 299.99, 50, 'ELECTRONICS', 'Laptop - 15 inch'),
('Supplier B', 49.99, 100, 'CLOTHING', 'T-Shirt - Size M'),
('Supplier A', 19.99, 200, 'FOOD', 'Organic Snacks'),
('Supplier C', 599.99, 25, 'FURNITURE', 'Office Chair'),
('Supplier B', 9.99, 150, 'OTHER', 'Notebook Set');

-- Insert sample data for Invoice
INSERT INTO invoice (source, destination, product_id) VALUES
('Supplier A Warehouse', 'Retailer 1 Store', 1),
('Supplier B Warehouse', 'Retailer 2 Store', 2),
('Supplier A Warehouse', 'Retailer 1 Store', 3);

-- Display table structures
DESCRIBE company;
DESCRIBE user;
DESCRIBE product;
DESCRIBE invoice;