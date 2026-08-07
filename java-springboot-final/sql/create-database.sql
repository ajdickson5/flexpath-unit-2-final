-- 1. Create database (Must be run separately, usually via psql or pgAdmin)
-- Postgres does not support IF NOT EXISTS for databases.
-- CREATE DATABASE web_shop;

-- 2. Drop tables in correct dependency order (child tables first)
DROP TABLE IF EXISTS order_items, orders, products, roles, users;

-- 3. Create tables using SERIAL for auto-incrementing IDs
CREATE TABLE users (
    username VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255)
);

CREATE TABLE roles (
    username VARCHAR(255) NOT NULL,
    role VARCHAR(250) NOT NULL,
    PRIMARY KEY (username, role),
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);

CREATE TABLE products (
    id SERIAL PRIMARY KEY, -- Changed from 'int primary key auto_increment'
    name VARCHAR(255),
    price DECIMAL(10, 2)
);

CREATE TABLE orders (
    id SERIAL PRIMARY KEY, -- Changed from 'int primary key auto_increment'
    username VARCHAR(255),
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);

CREATE TABLE order_items (
    id SERIAL PRIMARY KEY, -- Changed from 'int primary key auto_increment'
    order_id INT,
    product_id INT,
    quantity INT,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- 4. Insert data
INSERT INTO users (username, password) VALUES ('admin', '$2a$10$tBTfzHzjmQVKza3VSa5lsOX6/iL93xPVLlLXYg2FhT6a.jb1o6VDq');
INSERT INTO roles (username, role) VALUES ('admin', 'ADMIN');

INSERT INTO products (name, price) VALUES ('Apple', 0.99);
INSERT INTO products (name, price) VALUES ('Banana', 0.59);
INSERT INTO products (name, price) VALUES ('Cherry', 1.99);
INSERT INTO products (name, price) VALUES ('Date', 2.99);
INSERT INTO products (name, price) VALUES ('Elderberry', 3.99);

INSERT INTO orders (username) VALUES ('admin');
INSERT INTO orders (username) VALUES ('admin');
INSERT INTO orders (username) VALUES ('admin');
INSERT INTO orders (username) VALUES ('admin');
INSERT INTO orders (username) VALUES ('admin');
