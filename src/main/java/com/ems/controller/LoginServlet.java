package com.ems.controller;

import com.ems.dao.AuditLogDAO;
import com.ems.dao.OTPDAOImpl;
import com.ems.dao.UserDAOImpl;
import com.ems.model.User;
import com.ems.service.AuthService;
import com.ems.util.RecaptchaUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private AuthService authService;

    @Override
    public void init() throws ServletException {
        authService = new AuthService(new UserDAOImpl(), new OTPDAOImpl(), new AuditLogDAO());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String ipAddress = req.getRemoteAddr();
        String recaptchaResponse = req.getParameter("g-recaptcha-response");

        if (!RecaptchaUtil.verify(recaptchaResponse)) {
            req.setAttribute("error", "reCAPTCHA verification failed. Please try again.");
            req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
            return;
        }

        try {
            Optional<User> userOpt = authService.authenticate(email, password, ipAddress);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (user.isLocked()) {
                    req.setAttribute("error", "Account is locked due to multiple failed attempts.");
                    req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
                } else {
                    // Prevent Session Fixation
                    HttpSession oldSession = req.getSession(false);
                    if (oldSession != null) oldSession.invalidate();
                    HttpSession newSession = req.getSession(true);
                    
                    // Store user in temporary session attribute for OTP phase
                    newSession.setAttribute("temp_user", user);
                    
                    // Generate and Send OTP
                    authService.generateAndSendOTP(user);
                    
                    resp.sendRedirect(req.getContextPath() + "/verify-otp");
                }
            } else {
                req.setAttribute("error", "Invalid email or password.");
                req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}

