package com.ems.filter;

import com.ems.util.SecurityUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class CSRFFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession();

        // Generate token if not exists
        if (session.getAttribute("csrfToken") == null) {
            session.setAttribute("csrfToken", SecurityUtil.generateCSRFToken());
        }

        if ("POST".equalsIgnoreCase(req.getMethod())) {
            String sessionToken = (String) session.getAttribute("csrfToken");
            String requestToken = req.getParameter("csrfToken");

            if (sessionToken == null || !sessionToken.equals(requestToken)) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF token mismatch");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}

