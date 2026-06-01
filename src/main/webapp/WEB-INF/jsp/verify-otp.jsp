<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Verify OTP - StaffEase</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
            <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
        </head>

        <body class="auth-page">
            <div class="auth-card">
                <div class="auth-header">
                    <h2>2FA Verification</h2>
                    <p>Please enter the 6-digit code sent to <strong>${temp_user.email}</strong></p>
                </div>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <form action="${pageContext.request.contextPath}/verify-otp" method="POST">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <div class="form-group otp-group">
                        <input type="text" name="otp" maxlength="6" class="otp-input" placeholder="000000" required
                            autofocus>
                    </div>
                    <button type="submit" class="btn btn-primary btn-block">Verify & Continue</button>
                </form>

                <div class="auth-footer">
                    <p>Didn't receive the code? <a href="${pageContext.request.contextPath}/login">Try again</a></p>
                </div>
            </div>
        </body>

        </html>
