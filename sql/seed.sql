-- Sample Data for Employee Management System

-- 1. Insert Departments
INSERT INTO departments (dept_name) VALUES ('IT');
INSERT INTO departments (dept_name) VALUES ('HR');
INSERT INTO departments (dept_name) VALUES ('Sales');
INSERT INTO departments (dept_name) VALUES ('Finance');

-- 2. Insert Admin User (Role 1 is Admin)
-- Password is 'password123' hashed with BCrypt (Cost 12)
INSERT INTO users (email, password_hash, role_id) 
VALUES ('admin@ems.com', '$2a$12$R9h/lIPzHZ7.3m8.LdO87O5E8.G8Q8K4o4uLz6e6G6G6G6G6G6G6G', 1);

-- 3. Insert Admin Employee
INSERT INTO employees (user_id, first_name, last_name, dept_id, job_title, salary, email, phone, status, hire_date) 
VALUES (1, 'Admin', 'User', 1, 'CTO', 50000.00, 'admin@ems.com', '123-456-7890', 'Active', CURRENT_DATE);

-- 4. Insert Manager User (Role 2 is HR_Manager)
INSERT INTO users (email, password_hash, role_id) 
VALUES ('hr@ems.com', '$2a$12$R9h/lIPzHZ7.3m8.LdO87O5E8.G8Q8K4o4uLz6e6G6G6G6G6G6G6G', 2);

-- 5. Insert Manager Employee
INSERT INTO employees (user_id, first_name, last_name, dept_id, job_title, salary, email, phone, status, hire_date) 
VALUES (2, 'HR', 'Manager', 2, 'HR Lead', 45000.00, 'hr@ems.com', '987-654-3210', 'Active', CURRENT_DATE);
