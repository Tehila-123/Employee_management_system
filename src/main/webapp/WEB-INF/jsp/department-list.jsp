<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <title>Departments - EMS</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
            <style>
                .alert {
                    padding: 1rem;
                    margin-bottom: 1.5rem;
                    border-radius: 4px;
                    font-weight: 500;
                }
                .alert-error {
                    background-color: #FEE2E2;
                    color: #991B1B;
                    border-left: 4px solid #DC2626;
                }
                .alert-success {
                    background-color: #DCFCE7;
                    color: #166534;
                    border-left: 4px solid #22C55E;
                }
                .btn-delete-disabled {
                    display: inline-block;
                    padding: 0.35rem 0.7rem;
                    background-color: #D1D5DB;
                    color: #6B7280;
                    text-decoration: none;
                    border-radius: 4px;
                    cursor: not-allowed;
                    font-size: 0.9rem;
                }
                .btn-delete-disabled:hover {
                    background-color: #D1D5DB;
                    text-decoration: none;
                }
                .tooltip {
                    position: relative;
                    display: inline-block;
                }
                .tooltip .tooltiptext {
                    visibility: hidden;
                    width: 280px;
                    background-color: #333;
                    color: #fff;
                    text-align: center;
                    border-radius: 6px;
                    padding: 8px;
                    position: absolute;
                    z-index: 1;
                    bottom: 125%;
                    left: 50%;
                    margin-left: -140px;
                    opacity: 0;
                    transition: opacity 0.3s;
                    font-size: 0.85rem;
                    white-space: normal;
                    box-shadow: 0 2px 8px rgba(0,0,0,0.3);
                }
                .tooltip:hover .tooltiptext {
                    visibility: visible;
                    opacity: 1;
                }
            </style>
        </head>

        <body>
            <div class="dashboard-container">
                <aside class="sidebar">
                    <div class="sidebar-header">StaffEase</div>
                    <nav>
                        <a href="${pageContext.request.contextPath}/dashboard" class="nav-item">Dashboard</a>
                        <a href="${pageContext.request.contextPath}/employees" class="nav-item">Employees</a>
                        <a href="${pageContext.request.contextPath}/departments" class="nav-item active">Departments</a>
                        <a href="${pageContext.request.contextPath}/logout" class="nav-item"
                            style="color: #F87171; margin-top: auto;">Logout</a>
                    </nav>
                </aside>

                <main class="main-content">
                    <c:if test="${not empty sessionScope.error}">
                        <div class="alert alert-error">
                            ${sessionScope.error}
                            <% session.removeAttribute("error"); %>
                        </div>
                    </c:if>
                    <c:if test="${not empty sessionScope.success}">
                        <div class="alert alert-success">
                            ${sessionScope.success}
                            <% session.removeAttribute("success"); %>
                        </div>
                    </c:if>

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
                                    <th>Active Employees</th>
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
                                            <strong>${dept.activeEmployeeCount}</strong>
                                        </td>
                                        <td>
                                            <a
                                                href="${pageContext.request.contextPath}/departments/edit?id=${dept.deptId}">Edit</a>
                                            | 
                                            <c:choose>
                                                <c:when test="${dept.activeEmployeeCount > 0}">
                                                    <span class="tooltip">
                                                        <span class="btn-delete-disabled">Delete</span>
                                                        <span class="tooltiptext">Cannot delete: Department has ${dept.activeEmployeeCount} active employee(s)</span>
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <a href="${pageContext.request.contextPath}/departments/delete?id=${dept.deptId}"
                                                        onclick="return confirm('Are you sure you want to delete this department?')">Delete</a>
                                                </c:otherwise>
                                            </c:choose>
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