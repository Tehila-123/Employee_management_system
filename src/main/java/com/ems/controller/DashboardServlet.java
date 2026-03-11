package com.ems.controller;

import com.ems.dao.EmployeeDAOImpl;
import com.ems.dao.LeaveDAO;
import com.ems.dao.UserDAOImpl;
import com.google.gson.Gson;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private EmployeeDAOImpl employeeDAO;
    private LeaveDAO leaveDAO;

    @Override
    public void init() throws ServletException {
        employeeDAO = new EmployeeDAOImpl();
        leaveDAO = new LeaveDAO();
        departmentDAO = new com.ems.dao.DepartmentDAO();
    }

    private com.ems.dao.DepartmentDAO departmentDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int totalEmployees = employeeDAO.getTotalEmployeesCount();
            int pendingLeaves = leaveDAO.getAllPendingRequests().size();
            
            req.setAttribute("totalEmployees", totalEmployees);
            req.setAttribute("pendingLeaves", pendingLeaves);
            
            // Fetch real department stats
            java.util.Map<String, Integer> stats = departmentDAO.getDepartmentStats();
            Gson gson = new Gson();
            
            req.setAttribute("deptLabels", gson.toJson(stats.keySet()));
            req.setAttribute("deptData", gson.toJson(stats.values()));

            java.util.Map<String, Double> salaryStats = departmentDAO.getAverageSalaryPerDepartmentStats();
            req.setAttribute("salaryLabels", gson.toJson(salaryStats.keySet()));
            req.setAttribute("salaryData", gson.toJson(salaryStats.values()));
            
            req.getRequestDispatcher("/WEB-INF/jsp/dashboard.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}

