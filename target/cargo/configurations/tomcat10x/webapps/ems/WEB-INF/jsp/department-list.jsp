<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <title>Departments - EMS</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
        </head>

        <body>
            <div class="dashboard-container">
                <aside class="sidebar">
                    <div class="sidebar-header">EMS PRO</div>
                    <nav>
                        <a href="${pageContext.request.contextPath}/dashboard" class="nav-item">Dashboard</a>
                        <a href="${pageContext.request.contextPath}/employees" class="nav-item">Employees</a>
                        <a href="${pageContext.request.contextPath}/departments" class="nav-item active">Departments</a>
                        <a href="${pageContext.request.contextPath}/logout" class="nav-item"
                            style="color: #F87171; margin-top: auto;">Logout</a>
                    </nav>
                </aside>

                <main class="main-content">
                    <div
                        style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
                        <h1>Departments</h1>
                        <a href="${pageContext.request.contextPath}/departments/new" class="btn btn-primary">+ Add
                            Department</a>
                    </div>

                    <div class="table-container">
                        <table>
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Department Name</th>
                                    <th>Manager</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="dept" items="${departments}">
                                    <tr>
                                        <td>${dept.deptId}</td>
                                        <td><strong>${dept.deptName}</strong></td>
                                        <td>${dept.managerName}</td>
                                        <td>
                                            <a
                                                href="${pageContext.request.contextPath}/departments/edit?id=${dept.deptId}">Edit</a>
                                            | <a href="${pageContext.request.contextPath}/departments/delete?id=${dept.deptId}"
                                                onclick="return confirm('WARNING: You cannot delete a department that has active employees. Are you sure you want to proceed?')">Delete</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </main>
            </div>
        </body>

        </html>