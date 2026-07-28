-- ============================================
-- Micro Lending Circle Tracker - Database Schema
-- ============================================

CREATE DATABASE IF NOT EXISTS micro_lending_tracker;
USE micro_lending_tracker;

-- ---------------------------
-- Users (login accounts)
-- ---------------------------
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------
-- Circles (lending groups)
-- ---------------------------
CREATE TABLE IF NOT EXISTS circles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    monthly_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    start_date DATE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------
-- Members (people within a circle)
-- ---------------------------
CREATE TABLE IF NOT EXISTS members (
    id INT AUTO_INCREMENT PRIMARY KEY,
    circle_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    join_date DATE,
    payout_order INT DEFAULT 0,
    has_received_payout BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (circle_id) REFERENCES circles(id) ON DELETE CASCADE
);

-- ---------------------------
-- Contributions (monthly payments made by members)
-- ---------------------------
CREATE TABLE IF NOT EXISTS contributions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    circle_id INT NOT NULL,
    member_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    month_year VARCHAR(7) NOT NULL, -- format: YYYY-MM
    paid_date DATE,
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING / PAID
    FOREIGN KEY (circle_id) REFERENCES circles(id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
);

-- ---------------------------
-- Payouts (who received the pooled amount each cycle)
-- ---------------------------
CREATE TABLE IF NOT EXISTS payouts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    circle_id INT NOT NULL,
    member_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payout_date DATE,
    month_year VARCHAR(7) NOT NULL,
    FOREIGN KEY (circle_id) REFERENCES circles(id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
);

-- ---------------------------
-- Seed data (default login: admin / admin123)
-- ---------------------------
INSERT INTO users (username, password, full_name)
VALUES ('admin', 'admin123', 'Administrator')
ON DUPLICATE KEY UPDATE username = username;
