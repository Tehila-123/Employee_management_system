package com.ems.dao;

import com.ems.model.Employee;
import com.ems.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeDAOImpl implements EmployeeDAO {

    @Override
    public void createEmployee(Employee employee) throws SQLException {
        String sql = "INSERT INTO employees (user_id, first_name, last_name, dept_id, hire_date, profile_pic_path, job_title, salary, email, phone) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, employee.getUserId());
            stmt.setString(2, employee.getFirstName());
            stmt.setString(3, employee.getLastName());
            if (employee.getDeptId() != null) {
                stmt.setInt(4, employee.getDeptId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setDate(5, employee.getHireDate());
            stmt.setString(6, employee.getProfilePicPath());
            stmt.setString(7, employee.getJobTitle());
            stmt.setDouble(8, employee.getSalary());
            stmt.setString(9, employee.getEmail());
            stmt.setString(10, employee.getPhone());
            stmt.executeUpdate();
        }
    }

    @Override
    public void updateEmployee(Employee employee) throws SQLException {
        String sql = "UPDATE employees SET first_name = ?, last_name = ?, dept_id = ?, profile_pic_path = ?, job_title = ?, salary = ?, email = ?, phone = ? WHERE emp_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, employee.getFirstName());
            stmt.setString(2, employee.getLastName());
            if (employee.getDeptId() != null) {
                stmt.setInt(3, employee.getDeptId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setString(4, employee.getProfilePicPath());
            stmt.setString(5, employee.getJobTitle());
            stmt.setDouble(6, employee.getSalary());
            stmt.setString(7, employee.getEmail());
            stmt.setString(8, employee.getPhone());
            stmt.setInt(9, employee.getEmpId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void softDeleteEmployee(int empId) throws SQLException {
        String sql = "UPDATE employees SET status = 'Inactive' WHERE emp_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, empId);
            stmt.executeUpdate();
        }
    }

    @Override
    public Optional<Employee> getEmployeeById(int empId) throws SQLException {
        String sql = "SELECT e.*, d.dept_name FROM employees e LEFT JOIN departments d ON e.dept_id = d.dept_id WHERE e.emp_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, empId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEmployee(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Employee> getEmployeeByUserId(int userId) throws SQLException {
        String sql = "SELECT e.*, d.dept_name FROM employees e LEFT JOIN departments d ON e.dept_id = d.dept_id WHERE e.user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEmployee(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Employee> getAllEmployees(int offset, int limit) throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.*, d.dept_name FROM employees e LEFT JOIN departments d ON e.dept_id = d.dept_id LIMIT ? OFFSET ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(mapResultSetToEmployee(rs));
                }
            }
        }
        return employees;
    }

    @Override
    public List<Employee> searchEmployees(String query, int offset, int limit) throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.*, d.dept_name FROM employees e LEFT JOIN departments d ON e.dept_id = d.dept_id " +
                     "WHERE e.first_name LIKE ? OR e.last_name LIKE ? OR d.dept_name LIKE ? LIMIT ? OFFSET ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchTerm = "%" + query + "%";
            stmt.setString(1, searchTerm);
            stmt.setString(2, searchTerm);
            stmt.setString(3, searchTerm);
            stmt.setInt(4, limit);
            stmt.setInt(5, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(mapResultSetToEmployee(rs));
                }
            }
        }
        return employees;
    }

    @Override
    public int getTotalEmployeesCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM employees";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        Employee emp = new Employee();
        emp.setEmpId(rs.getInt("emp_id"));
        emp.setUserId(rs.getInt("user_id"));
        emp.setFirstName(rs.getString("first_name"));
        emp.setLastName(rs.getString("last_name"));
        int deptId = rs.getInt("dept_id");
        if (rs.wasNull()) {
            emp.setDeptId(null);
        } else {
            emp.setDeptId(deptId);
        }
        emp.setDeptName(rs.getString("dept_name"));
        emp.setStatus(rs.getString("status"));
        emp.setProfilePicPath(rs.getString("profile_pic_path"));
        emp.setHireDate(rs.getDate("hire_date"));
        emp.setJobTitle(rs.getString("job_title"));
        emp.setSalary(rs.getDouble("salary"));
        emp.setEmail(rs.getString("email"));
        emp.setPhone(rs.getString("phone"));
        return emp;
    }
}

