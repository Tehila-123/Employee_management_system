package com.ems.filter;

import com.ems.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebFilter("/*")
public class RoleFilter implements Filter {
    private static final List<String> PUBLIC_PATHS = Arrays.asList("/login", "/register", "/verify-otp", "/assets/", "/logout");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);
        String path = req.getRequestURI().substring(req.getContextPath().length());

        // Allow public paths
        boolean isPublic = PUBLIC_PATHS.stream().anyMatch(path::startsWith);
        if (isPublic || path.equals("/") || path.isEmpty()) {
            chain.doFilter(request, response);
            return;
        }

        // Check if user is logged in
        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        String role = user.getRoleName();

        // RBAC Logic
        if (path.startsWith("/admin") && !"Admin".equals(role)) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Admins Only");
            return;
        }

        if (path.startsWith("/hr") && !("HR_Manager".equals(role) || "Admin".equals(role))) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: HR/Admin Only");
            return;
        }

        chain.doFilter(request, response);
    }
}

