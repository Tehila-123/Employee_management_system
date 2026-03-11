package com.ems.dao;

import com.ems.model.Employee;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface EmployeeDAO {
    void createEmployee(Employee employee) throws SQLException;
    void updateEmployee(Employee employee) throws SQLException;
    void softDeleteEmployee(int empId) throws SQLException;
    Optional<Employee> getEmployeeById(int empId) throws SQLException;
    Optional<Employee> getEmployeeByUserId(int userId) throws SQLException;
    List<Employee> getAllEmployees(int offset, int limit) throws SQLException;
    List<Employee> searchEmployees(String query, int offset, int limit) throws SQLException;
    int getTotalEmployeesCount() throws SQLException;
}

