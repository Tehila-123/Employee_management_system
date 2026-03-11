<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <title>Audit Logs - EMS</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
        </head>

        <body>
            <div class="dashboard-container">
                <aside class="sidebar">
                    <div class="sidebar-header">EMS PRO</div>
                    <nav>
                        <a href="${pageContext.request.contextPath}/dashboard" class="nav-item">Dashboard</a>
                        <a href="${pageContext.request.contextPath}/employees" class="nav-item">Employees</a>
                        <a href="${pageContext.request.contextPath}/admin/audit-logs" class="nav-item active">Audit
                            Logs</a>
                        <a href="${pageContext.request.contextPath}/logout" class="nav-item"
                            style="color: #F87171; margin-top: 2rem;">Logout</a>
                    </nav>
                </aside>

                <main class="main-content">
                    <h1>System Audit Logs</h1>
                    <p style="color: var(--text-muted); margin-bottom: 2rem;">Security and administrative activity
                        tracking.</p>

                    <div class="table-container">
                        <table>
                            <thead>
                                <tr>
                                    <th>Time</th>
                                    <th>User</th>
                                    <th>Action</th>
                                    <th>Description</th>
                                    <th>IP Address</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="log" items="${logs}">
                                    <tr>
                                        <td style="font-size: 0.75rem;">${log.timestamp}</td>
                                        <td>${log.userEmail != null ? log.userEmail : 'SYSTEM'}</td>
                                        <td><span style="font-weight: 600;">${log.action}</span></td>
                                        <td style="font-size: 0.8125rem;">${log.description}</td>
                                        <td style="font-family: monospace;">${log.ipAddress}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </main>
            </div>
        </body>

        </html>
