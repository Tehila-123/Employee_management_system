<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <title>${not empty department ? 'Edit' : 'Add'} Department - EMS</title>
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
                    </nav>
                </aside>

                <main class="main-content">
                    <header style="margin-bottom: 2rem;">
                        <h1>${not empty department ? 'Edit Department' : 'Add New Department'}</h1>
                        <p style="color: var(--text-muted)">Define department name and assign a manager.</p>
                    </header>

                    <div class="auth-card" style="max-width: 500px; margin: 0;">
                        <form
                            action="${pageContext.request.contextPath}/departments/${not empty department ? 'update' : 'insert'}"
                            method="POST">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <c:if test="${not empty department}">
                                <input type="hidden" name="deptId" value="${department.deptId}">
                            </c:if>

                            <div class="form-group">
                                <label for="deptName">Department Name</label>
                                <input type="text" id="deptName" name="deptName" value="${department.deptName}"
                                    required>
                            </div>

                            <div class="form-group">
                                <label for="managerId">Department Manager</label>
                                <select name="managerId" id="managerId" class="form-group"
                                    style="width: 100%; padding: 0.625rem; border: 1px solid var(--border); border-radius: 0.5rem;">
                                    <option value="0">None</option>
                                    <c:forEach var="emp" items="${employees}">
                                        <option value="${emp.empId}" ${department.managerId==emp.empId ? 'selected' : ''
                                            }>
                                            ${emp.firstName} ${emp.lastName}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div style="display: flex; gap: 1rem; margin-top: 2rem;">
                                <button type="submit" class="btn btn-primary">${not empty department ? 'Update' :
                                    'Create'} Department</button>
                                <a href="${pageContext.request.contextPath}/departments" class="btn"
                                    style="background: #E5E7EB;">Cancel</a>
                            </div>
                        </form>
                    </div>
                </main>
            </div>
        </body>

        </html>