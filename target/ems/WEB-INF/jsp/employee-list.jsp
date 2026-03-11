<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <title>Employee List - EMS</title>
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
                        <a href="${pageContext.request.contextPath}/departments" class="nav-item">Departments</a>
                        <a href="${pageContext.request.contextPath}/logout" class="nav-item"
                            style="color: #F87171; margin-top: auto;">Logout</a>
                    </nav>
                </aside>

                <main class="main-content">
                    <div
                        style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
                        <h1>Employees</h1>
                        <div style="display: flex; gap: 1rem;">
                            <form action="${pageContext.request.contextPath}/employees" method="GET"
                                style="display: flex; gap: 0.5rem;">
                                <input type="text" name="q" placeholder="Search employees..." value="${param.q}"
                                    class="form-group" style="margin-bottom: 0; min-width: 300px;">
                                <button type="submit" class="btn btn-primary">Search</button>
                            </form>
                            <a href="${pageContext.request.contextPath}/employees/new" class="btn btn-primary">+ Add
                                Employee</a>
                        </div>
                    </div>

                    <div class="table-container">
                        <table>
                            <thead>
                                <tr>
                                    <th>Photo</th>
                                    <th>Name</th>
                                    <th>Job Title</th>
                                    <th>Department</th>
                                    <th>Salary</th>
                                    <th>Hired</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="emp" items="${employees}">
                                    <tr>
                                        <td>
                                            <img src="${pageContext.request.contextPath}/${not empty emp.profilePicPath ? emp.profilePicPath : 'assets/img/default-avatar.png'}"
                                                alt="Avatar"
                                                style="width: 32px; height: 32px; border-radius: 50%; object-fit: cover;">
                                        </td>
                                        <td><strong>${emp.firstName} ${emp.lastName}</strong></td>
                                        <td>${emp.jobTitle}</td>
                                        <td>${emp.deptName}</td>
                                        <td>$${emp.salary}</td>
                                        <td>${emp.hireDate}</td>
                                        <td><span
                                                class="status-badge ${emp.status == 'Active' ? 'status-active' : 'status-inactive'}">${emp.status}</span>
                                        </td>
                                        <td>
                                            <a
                                                href="${pageContext.request.contextPath}/employees/view?id=${emp.empId}">View</a>
                                            | <a
                                                href="${pageContext.request.contextPath}/employees/edit?id=${emp.empId}">Edit</a>
                                            | <a href="${pageContext.request.contextPath}/employees/delete?id=${emp.empId}"
                                                onclick="return confirm('Are you sure?')">Delete</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <!-- Pagination -->
                    <div style="margin-top: 1.5rem; display: flex; justify-content: center; gap: 0.5rem;">
                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <a href="?page=${i}&q=${param.q}"
                                class="btn ${i == currentPage ? 'btn-primary' : 'btn-secondary'}">${i}</a>
                        </c:forEach>
                    </div>
                </main>
            </div>
        </body>

        </html>