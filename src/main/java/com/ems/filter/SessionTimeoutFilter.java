package com.ems.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class SessionTimeoutFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        // 15 minutes = 900 seconds
        if (session != null) {
            session.setMaxInactiveInterval(900);
            
            if (session.getAttribute("user") != null) {
                long lastAccessedTime = session.getLastAccessedTime();
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastAccessedTime > 900000) {
                    session.invalidate();
                    res.sendRedirect(req.getContextPath() + "/login?error=Session expired");
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }
}

