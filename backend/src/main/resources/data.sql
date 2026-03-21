
INSERT INTO category (name) VALUES ('Electronics');
INSERT INTO category (name) VALUES ('Home & Kitchen');
INSERT INTO category (name) VALUES ('Fitness');
INSERT INTO category (name) VALUES ('Books');

INSERT INTO product (name, description, price, stock, category_id) VALUES
                                                                       ('Wireless Mouse', 'Ergonomic 2.4GHz wireless mouse', 25.99, 50, 1),
                                                                       ('Mechanical Keyboard', 'RGB backlit gaming keyboard', 89.50, 30, 1),
                                                                       ('Noise Cancelling Headphones', 'Over-ear bluetooth headphones', 199.99, 15, 1),
                                                                       ('USB-C Hub', '7-in-1 multi-port adapter', 35.00, 100, 1),
                                                                       ('Smart Watch', 'Waterproof fitness tracker with GPS', 120.00, 25, 1);

INSERT INTO product (name, description, price, stock, category_id) VALUES
                                                                       ('Air Fryer', '5.5L digital air fryer', 115.00, 20, 2),
                                                                       ('Electric Kettle', '1.7L stainless steel kettle', 29.99, 40, 2),
                                                                       ('Coffee Maker', 'Programmable drip coffee machine', 45.00, 12, 2),
                                                                       ('Blender', 'High-speed professional blender', 85.00, 18, 2),
                                                                       ('Kitchen Knife Set', '6-piece stainless steel set', 59.90, 10, 2);

INSERT INTO product (name, description, price, stock, category_id) VALUES
                                                                       ('Yoga Mat', 'Eco-friendly non-slip yoga mat', 22.00, 60, 3),
                                                                       ('Dumbbell Set', 'Adjustable 20kg dumbbell pair', 75.00, 8, 3),
                                                                       ('Resistance Bands', 'Set of 5 exercise bands', 15.50, 150, 3),
                                                                       ('Foam Roller', 'High-density muscle massage roller', 19.00, 35, 3),
                                                                       ('Jump Rope', 'Weighted handles skipping rope', 12.00, 80, 3);

INSERT INTO product (name, description, price, stock, category_id) VALUES
                                                                       ('Clean Code', 'A Handbook of Agile Software Craftsmanship', 38.00, 25, 4),
                                                                       ('Effective Java', 'Best practices for the Java platform', 45.00, 20, 4),
                                                                       ('Spring in Action', 'Comprehensive guide to Spring Framework', 52.50, 15, 4),
                                                                       ('The Pragmatic Programmer', 'Your journey to mastery', 40.00, 12, 4),
                                                                       ('Design Patterns', 'Elements of Reusable Object-Oriented Software', 55.00, 10, 4);