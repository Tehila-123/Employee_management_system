package com.ems.controller;

import com.ems.dao.EmployeeDAOImpl;
import com.ems.dao.LeaveDAO;
import com.ems.model.Employee;
import com.ems.model.LeaveRequest;
import com.ems.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/leave/*")
public class LeaveServlet extends HttpServlet {
    private LeaveDAO leaveDAO;
    private EmployeeDAOImpl employeeDAO;

    @Override
    public void init() throws ServletException {
        leaveDAO = new LeaveDAO();
        employeeDAO = new EmployeeDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getPathInfo();
        User user = (User) req.getSession().getAttribute("user");
        
        try {
            if (action == null || action.equals("/")) {
                if ("Employee".equals(user.getRoleName())) {
                    employeeDAO.getEmployeeByUserId(user.getUserId()).ifPresent(emp -> {
                        try {
                            req.setAttribute("leaves", leaveDAO.getLeaveRequestsByEmployee(emp.getEmpId()));
                        } catch (SQLException e) {}
                    });
                } else {
                    req.setAttribute("leaves", leaveDAO.getAllPendingRequests());
                }
                req.getRequestDispatcher("/WEB-INF/jsp/leave-list.jsp").forward(req, resp);
            } else if (action.equals("/new")) {
                req.getRequestDispatcher("/WEB-INF/jsp/leave-form.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getPathInfo();
        User user = (User) req.getSession().getAttribute("user");

        try {
            if (action.equals("/apply")) {
                applyLeave(req, resp, user);
            } else if (action.equals("/approve") || action.equals("/reject")) {
                handleLeaveAction(req, resp, action.replace("/", ""), user);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void applyLeave(HttpServletRequest req, HttpServletResponse resp, User user) throws SQLException, IOException {
        employeeDAO.getEmployeeByUserId(user.getUserId()).ifPresent(emp -> {
            LeaveRequest lr = new LeaveRequest();
            lr.setEmpId(emp.getEmpId());
            lr.setLeaveType(req.getParameter("leaveType"));
            lr.setStartDate(Date.valueOf(req.getParameter("startDate")));
            lr.setEndDate(Date.valueOf(req.getParameter("endDate")));
            lr.setReason(req.getParameter("reason"));
            try {
                leaveDAO.createLeaveRequest(lr);
            } catch (SQLException e) {}
        });
        resp.sendRedirect(req.getContextPath() + "/leave");
    }

    private void handleLeaveAction(HttpServletRequest req, HttpServletResponse resp, String action, User user) throws SQLException, IOException {
        int leaveId = Integer.parseInt(req.getParameter("id"));
        String status = action.equals("approve") ? "Approved" : "Rejected";
        employeeDAO.getEmployeeByUserId(user.getUserId()).ifPresent(emp -> {
            try {
                leaveDAO.updateLeaveStatus(leaveId, status, emp.getEmpId());
            } catch (SQLException e) {}
        });
        resp.sendRedirect(req.getContextPath() + "/leave");
    }
}

