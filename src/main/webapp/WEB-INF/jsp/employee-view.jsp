<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <title>View Employee - EMS</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
            <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
        </head>

        <body>
            <div class="dashboard-container">
                <aside class="sidebar">
                    <div class="sidebar-header">EMS PRO</div>
                    <nav>
                        <a href="${pageContext.request.contextPath}/dashboard" class="nav-item">Dashboard</a>
                        <a href="${pageContext.request.contextPath}/employees" class="nav-item active">Employees</a>
                    </nav>
                </aside>

                <main class="main-content">
                    <div
                        style="display: flex; justify-content: space-between; align-items: start; margin-bottom: 2rem;">
                        <div>
                            <h1>Employee Profile</h1>
                            <p style="color: var(--text-muted)">Detailed information for ${employee.firstName}
                                ${employee.lastName}</p>
                        </div>
                        <c:if test="${sessionScope.user.roleName != 'Employee'}">
                            <a href="${pageContext.request.contextPath}/employees/edit?id=${employee.empId}"
                                class="btn btn-primary">Edit Profile</a>
                        </c:if>
                    </div>

                    <div class="chart-container" style="display: flex; gap: 2rem; padding: 2.5rem;">
                        <div style="flex: 0 0 150px;">
                            <img src="${pageContext.request.contextPath}/${not empty employee.profilePicPath ? employee.profilePicPath : 'assets/img/default-avatar.png'}"
                                style="width: 150px; height: 150px; border-radius: 1rem; object-fit: cover; border: 4px solid var(--bg-main);">
                        </div>
                        <div style="flex: 1;">
                            <div class="stats-grid" style="grid-template-columns: 1fr 1fr; margin-bottom: 0;">
                                <div>
                                    <label style="display:block; font-size: 0.75rem; color: var(--text-muted);">FULL
                                        NAME</label>
                                    <span style="font-weight: 600;">${employee.firstName} ${employee.lastName}</span>
                                </div>
                                <div>
                                    <label
                                        style="display:block; font-size: 0.75rem; color: var(--text-muted);">DEPARTMENT</label>
                                    <span style="font-weight: 600;">${employee.deptName}</span>
                                </div>
                                <div style="margin-top: 1.5rem;">
                                    <label style="display:block; font-size: 0.75rem; color: var(--text-muted);">HIRE
                                        DATE</label>
                                    <span style="font-weight: 600;">${employee.hireDate}</span>
                                </div>
                                <div style="margin-top: 1.5rem;">
                                    <label
                                        style="display:block; font-size: 0.75rem; color: var(--text-muted);">STATUS</label>
                                    <span
                                        class="status-badge ${employee.status == 'Active' ? 'status-active' : 'status-inactive'}">${employee.status}</span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div style="margin-top: 2rem;">
                        <a href="${pageContext.request.contextPath}/employees" class="btn"
                            style="background: #E5E7EB;">← Back to List</a>
                        <c:if test="${sessionScope.user.roleName == 'Admin'}">
                            <form action="${pageContext.request.contextPath}/employees/delete" method="POST"
                                style="display: inline; margin-left: 1rem;">
                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                <input type="hidden" name="id" value="${employee.empId}">
                                <button type="submit" class="btn" style="background: #FEE2E2; color: #991B1B;"
                                    onclick="return confirm('Archive this employee?')">Archive Employee</button>
                            </form>
                        </c:if>
                    </div>
                </main>
            </div>
        </body>

        </html>
