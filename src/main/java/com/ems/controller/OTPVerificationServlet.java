package com.ems.controller;

import com.ems.dao.AuditLogDAO;
import com.ems.dao.OTPDAOImpl;
import com.ems.dao.UserDAOImpl;
import com.ems.model.User;
import com.ems.service.AuthService;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/verify-otp")
public class OTPVerificationServlet extends HttpServlet {
    private AuthService authService;

    @Override
    public void init() throws ServletException {
        authService = new AuthService(new UserDAOImpl(), new OTPDAOImpl(), new AuditLogDAO());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession().getAttribute("temp_user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/jsp/verify-otp.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("temp_user");
        String otp = req.getParameter("otp");
        String ipAddress = req.getRemoteAddr();

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            if (authService.verifyOTP(user.getUserId(), otp, ipAddress)) {
                session.removeAttribute("temp_user");
                session.setAttribute("user", user);
                resp.sendRedirect(req.getContextPath() + "/dashboard");
            } else {
                req.setAttribute("error", "Invalid or expired OTP.");
                req.getRequestDispatcher("/WEB-INF/jsp/verify-otp.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}

