-- INSERT INTO customers (
--     first_name, last_name, email, phone, address, city, state, zip, country, join_date, membership_type, total_spent
-- ) VALUES
-- ('John', 'Smith', 'john.smith@email.com', '555-0101', '123 Main St', 'New York', 'NY', '10001', 'USA', '2023-01-15', 'Premium', 15000.00),
-- ('Emma', 'Johnson', 'emma.j@email.com', '555-0102', '456 Oak Ave', 'Los Angeles', 'CA', '90001', 'USA', '2023-02-20', 'Standard', 8500.00),
-- ('Michael', 'Williams', 'michael.w@email.com', '555-0103', '789 Pine St', 'Chicago', 'IL', '60601', 'USA', '2023-03-10', 'Premium', 12000.00),
-- ('Sophia', 'Brown', 'sophia.b@email.com', '555-0104', '321 Elm St', 'Houston', 'TX', '77001', 'USA', '2023-04-05', 'Standard', 6500.00),
-- ('James', 'Davis', 'james.d@email.com', '555-0105', '654 Maple Dr', 'Phoenix', 'AZ', '85001', 'USA', '2023-05-12', 'Basic', 3200.00);


INSERT INTO products (product_id, product_name, category, subcategory, brand, unit_price, cost_price, stock_quantity, reorder_level) VALUES
(101, 'Laptop Pro 15', 'Electronics', 'Computers', 'TechBrand', 1299.99, 900.00, 50, 10),
(102, 'Wireless Mouse', 'Electronics', 'Accessories', 'TechBrand', 29.99, 15.00, 200, 50),
(103, 'USB-C Cable', 'Electronics', 'Accessories', 'TechBrand', 19.99, 8.00, 500, 100),
(104, 'Office Chair', 'Furniture', 'Seating', 'ComfortCo', 299.99, 180.00, 30, 5),
(105, 'Standing Desk', 'Furniture', 'Desks', 'ComfortCo', 599.99, 380.00, 15, 3),
(106, 'Notebook Set', 'Stationery', 'Paper Products', 'WriteCo', 12.99, 5.00, 1000, 200),
(107, 'Pen Pack (10)', 'Stationery', 'Writing Tools', 'WriteCo', 8.99, 3.50, 800, 150),
(108, 'Monitor 27"', 'Electronics', 'Displays', 'TechBrand', 349.99, 220.00, 40, 8);


-- Insert sample orders with recent dates for testing
-- INSERT INTO orders (order_id, customer_id, order_date, order_status, total_amount, payment_method, shipping_address) 
-- VALUES
-- (1001, 1, CURRENT_TIMESTAMP - INTERVAL '5 days', 'Delivered', 1329.98, 'Credit Card', '123 Main St, New York, NY 10001'),
-- (1002, 2, CURRENT_TIMESTAMP - INTERVAL '8 days', 'Delivered', 329.98, 'PayPal', '456 Oak Ave, Los Angeles, CA 90001'),
-- (1003, 3, CURRENT_TIMESTAMP - INTERVAL '12 days', 'Shipped', 649.98, 'Credit Card', '789 Pine St, Chicago, IL 60601'),
-- (1004, 1, CURRENT_TIMESTAMP - INTERVAL '3 days', 'Processing', 899.97, 'Credit Card', '123 Main St, New York, NY 10001'),
-- (1005, 4, CURRENT_TIMESTAMP - INTERVAL '7 days', 'Delivered', 42.98, 'Debit Card', '321 Elm St, Houston, TX 77001'),
-- (1006, 5, CURRENT_TIMESTAMP - INTERVAL '2 days', 'Shipped', 1949.97, 'Credit Card', '654 Maple Dr, Phoenix, AZ 85001');


-- INSERT INTO order_items (order_item_id, order_id, product_id, quantity, unit_price, discount_percent, line_total) VALUES
-- (1, 1001, 101, 1, 1299.99, 0, 1299.99),
-- (2, 1001, 102, 1, 29.99, 0, 29.99),
-- (3, 1002, 104, 1, 299.99, 10, 269.99),
-- (4, 1002, 106, 4, 12.99, 0, 51.96),
-- (5, 1003, 105, 1, 599.99, 0, 599.99),
-- (6, 1003, 103, 2, 19.99, 0, 39.98),
-- (7, 1004, 108, 2, 349.99, 0, 699.98),
-- (8, 1004, 102, 5, 29.99, 20, 119.96),
-- (9, 1005, 106, 2, 12.99, 0, 25.98),
-- (10, 1005, 107, 2, 8.99, 5, 17.00),
-- (11, 1006, 101, 1, 1299.99, 0, 1299.99),
-- (12, 1006, 105, 1, 599.99, 0, 599.99),
-- (13, 1006, 103, 3, 19.99, 10, 53.97);


-- -- Insert sample customer events with JSONB data
-- INSERT INTO customer_events (customer_id, event_timestamp, event_data)
-- SELECT 1, '2024-10-01 10:00:00'::timestamp, '{"event_type": "page_view", "page": "product_details", "product_id": 101, "session_duration": 120}'::jsonb
-- UNION ALL
-- SELECT 1, '2024-10-01 10:25:00'::timestamp, '{"event_type": "add_to_cart", "product_id": 101, "quantity": 1, "price": 1299.99}'::jsonb
-- UNION ALL
-- SELECT 2, '2024-10-03 14:00:00'::timestamp, '{"event_type": "search", "query": "office chair", "results_count": 15}'::jsonb
-- UNION ALL
-- SELECT 3, '2024-10-05 09:30:00'::timestamp, '{"event_type": "purchase", "order_id": 1003, "total": 649.98, "items": 2}'::jsonb;
