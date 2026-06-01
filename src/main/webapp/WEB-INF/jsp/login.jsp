<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Login - StaffEase</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
            <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
            <script src="https://www.google.com/recaptcha/api.js" async defer></script>
        </head>

        <body class="auth-page">
            <div class="auth-card">
                <div class="auth-header">
                    <h2>Welcome Back</h2>
                    <p>Enter your credentials to access your account</p>
                </div>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>
                <c:if test="${not empty param.success}">
                    <div class="alert alert-success">${param.success}</div>
                </c:if>

                <form action="${pageContext.request.contextPath}/login" method="POST">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <div class="form-group">
                        <label for="email">Email Address</label>
                        <input type="email" id="email" name="email" required placeholder="name@company.com">
                    </div>
                    <div class="form-group">
                        <label for="password">Password</label>
                        <input type="password" id="password" name="password" required placeholder="••••••••">
                    </div>
                    <div class="form-group" style="margin-top: 1rem;">
                        <div class="g-recaptcha" data-sitekey="6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI"></div>
                    </div>
                    <button type="submit" class="btn btn-primary btn-block" style="margin-top: 1rem;">Sign In</button>
                </form>

                <div class="auth-footer">
                    <p>Don't have an account? <a href="${pageContext.request.contextPath}/register">Create one</a></p>
                </div>
            </div>
        </body>

        </html>