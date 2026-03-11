package com.ems.controller;

import com.ems.dao.EmployeeDAOImpl;
import com.ems.model.Employee;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/employees/export")
public class ExportServlet extends HttpServlet {
    private EmployeeDAOImpl employeeDAO;

    @Override
    public void init() throws ServletException {
        employeeDAO = new EmployeeDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/csv");
        resp.setHeader("Content-Disposition", "attachment; filename=\"employees.csv\"");

        try (PrintWriter writer = resp.getWriter()) {
            writer.println("ID,Name,Department,Hire Date,Status");
            List<Employee> employees = employeeDAO.getAllEmployees(0, Integer.MAX_VALUE);
            for (Employee e : employees) {
                writer.printf("%d,%s %s,%s,%s,%s%n", 
                    e.getEmpId(), e.getFirstName(), e.getLastName(), 
                    e.getDeptName(), e.getHireDate(), e.getStatus());
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}

