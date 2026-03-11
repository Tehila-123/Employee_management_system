package com.ems.controller;

import com.ems.dao.AuditLogDAO;
import com.ems.dao.EmployeeDAOImpl;
import com.ems.dao.UserDAOImpl;
import com.ems.model.Employee;
import com.ems.model.User;
import com.ems.util.RecaptchaUtil;
import com.ems.util.SecurityUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private UserDAOImpl userDAO;
    private EmployeeDAOImpl employeeDAO;
    private AuditLogDAO auditLogDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAOImpl();
        employeeDAO = new EmployeeDAOImpl();
        auditLogDAO = new AuditLogDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String recaptchaResponse = req.getParameter("g-recaptcha-response");
        String ipAddress = req.getRemoteAddr();

        // Validate reCAPTCHA
        if (!RecaptchaUtil.verify(recaptchaResponse)) {
            req.setAttribute("error", "reCAPTCHA verification failed.");
            req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
            return;
        }

        // Validate Password Strength
        if (!SecurityUtil.isValidPassword(password)) {
            req.setAttribute("error", "Password must be at least 8 chars, incl. uppercase, lowercase, digit, and special char.");
            req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
            return;
        }

        try {
            if (userDAO.existsByEmail(email)) {
                req.setAttribute("error", "Email already registered.");
                req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
                return;
            }

            // Create User
            User user = new User();
            user.setEmail(email);
            user.setPasswordHash(SecurityUtil.hashPassword(password));
            user.setRoleId(3); // Default to Employee role
            userDAO.createUser(user);

            // Create Employee Profile
            Employee emp = new Employee();
            emp.setUserId(user.getUserId());
            emp.setFirstName(firstName);
            emp.setLastName(lastName);
            emp.setHireDate(new Date(System.currentTimeMillis()));
            employeeDAO.createEmployee(emp);

            auditLogDAO.createLog(user.getUserId(), "REGISTRATION", "New user registered: " + email, ipAddress);
            
            resp.sendRedirect(req.getContextPath() + "/login?success=Registration successful. Please login.");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}

