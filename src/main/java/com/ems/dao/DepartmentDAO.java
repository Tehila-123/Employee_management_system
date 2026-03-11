package com.ems.dao;

import com.ems.model.Department;
import com.ems.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DepartmentDAO {
    public void createDepartment(Department dept) throws SQLException {
        String sql = "INSERT INTO departments (dept_name, manager_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dept.getDeptName());
            if (dept.getManagerId() > 0) stmt.setInt(2, dept.getManagerId()); else stmt.setNull(2, java.sql.Types.INTEGER);
            stmt.executeUpdate();
        }
    }

    public List<Department> getAllDepartments() throws SQLException {
        List<Department> depts = new ArrayList<>();
        String sql = "SELECT d.*, e.first_name, e.last_name FROM departments d LEFT JOIN employees e ON d.manager_id = e.emp_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Department d = new Department();
                d.setDeptId(rs.getInt("dept_id"));
                d.setDeptName(rs.getString("dept_name"));
                d.setManagerId(rs.getInt("manager_id"));
                d.setManagerName(rs.getString("first_name") != null ? rs.getString("first_name") + " " + rs.getString("last_name") : "None");
                depts.add(d);
            }
        }
        return depts;
    }

    public void updateDepartment(Department dept) throws SQLException {
        String sql = "UPDATE departments SET dept_name = ?, manager_id = ? WHERE dept_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dept.getDeptName());
            if (dept.getManagerId() > 0) stmt.setInt(2, dept.getManagerId()); else stmt.setNull(2, java.sql.Types.INTEGER);
            stmt.setInt(3, dept.getDeptId());
            stmt.executeUpdate();
        }
    }

    public Optional<Department> getDepartmentById(int id) throws SQLException {
        String sql = "SELECT * FROM departments WHERE dept_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Department d = new Department();
                    d.setDeptId(rs.getInt("dept_id"));
                    d.setDeptName(rs.getString("dept_name"));
                    d.setManagerId(rs.getInt("manager_id"));
                    return Optional.of(d);
                }
            }
        }
        return Optional.empty();
    }

    public void deleteDepartment(int id) throws SQLException {
        String sql = "DELETE FROM departments WHERE dept_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public java.util.Map<String, Integer> getDepartmentStats() throws SQLException {
        java.util.Map<String, Integer> stats = new java.util.LinkedHashMap<>();
        String sql = "SELECT d.dept_name, COUNT(e.emp_id) as count FROM departments d " +
                     "LEFT JOIN employees e ON d.dept_id = e.dept_id GROUP BY d.dept_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                stats.put(rs.getString("dept_name"), rs.getInt("count"));
            }
        }
        return stats;
    }

    public java.util.Map<String, Double> getAverageSalaryPerDepartmentStats() throws SQLException {
        java.util.Map<String, Double> stats = new java.util.LinkedHashMap<>();
        String sql = "SELECT d.dept_name, COALESCE(AVG(e.salary), 0) as avg_salary FROM departments d " +
                     "LEFT JOIN employees e ON d.dept_id = e.dept_id GROUP BY d.dept_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                stats.put(rs.getString("dept_name"), rs.getDouble("avg_salary"));
            }
        }
        return stats;
    }
}

