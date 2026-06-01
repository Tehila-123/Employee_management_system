<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <title>Leave Requests - EMS</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
        </head>

        <body>
            <div class="dashboard-container">
                <aside class="sidebar">
                    <div class="sidebar-header">StaffEase</div>
                    <nav>
                        <a href="${pageContext.request.contextPath}/dashboard" class="nav-item">Dashboard</a>
                        <a href="${pageContext.request.contextPath}/employees" class="nav-item">Employees</a>
                        <a href="${pageContext.request.contextPath}/departments" class="nav-item">Departments</a>
                        <a href="${pageContext.request.contextPath}/leave" class="nav-item active">Leave Requests</a>
                        <a href="${pageContext.request.contextPath}/logout" class="nav-item"
                            style="color: #F87171; margin-top: auto;">Logout</a>
                    </nav>
                </aside>

                <main class="main-content">
                    <div
                        style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
                        <h1>Leave Requests</h1>
                        <c:if test="${sessionScope.user.roleName == 'Employee'}">
                            <a href="${pageContext.request.contextPath}/leave/new" class="btn btn-primary">Request
                                Leave</a>
                        </c:if>
                    </div>

                    <div class="table-container">
                        <table>
                            <thead>
                                <tr>
                                    <th>Employee</th>
                                    <th>Type</th>
                                    <th>Duration</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="lr" items="${leaves}">
                                    <tr>
                                        <td>${sessionScope.user.roleName == 'Employee' ? 'You' : lr.empName}</td>
                                        <td>${lr.leaveType}</td>
                                        <td>${lr.startDate} to ${lr.endDate}</td>
                                        <td><span
                                                class="status-badge ${lr.status == 'Approved' ? 'status-active' : (lr.status == 'Rejected' ? 'status-inactive' : '')}">${lr.status}</span>
                                        </td>
                                        <td>
                                            <c:if
                                                test="${lr.status == 'Pending' && sessionScope.user.roleName != 'Employee'}">
                                                <form action="${pageContext.request.contextPath}/leave/approve"
                                                    method="POST" style="display: inline;">
                                                    <input type="hidden" name="id" value="${lr.leaveId}">
                                                    <button type="submit" class="btn btn-primary"
                                                        style="padding: 0.25rem 0.5rem; font-size: 0.75rem;">Approve</button>
                                                </form>
                                                <form action="${pageContext.request.contextPath}/leave/reject"
                                                    method="POST" style="display: inline;">
                                                    <input type="hidden" name="id" value="${lr.leaveId}">
                                                    <button type="submit" class="btn"
                                                        style="padding: 0.25rem 0.5rem; font-size: 0.75rem; background: #E5E7EB;">Reject</button>
                                                </form>
                                            </c:if>
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
