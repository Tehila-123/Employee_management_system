<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <title>${not empty employee ? 'Edit' : 'Add'} Employee - EMS</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
            <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
        </head>

        <body>
            <div class="dashboard-container">
                <aside class="sidebar">
                    <div class="sidebar-header">StaffEase</div>
                    <nav>
                        <a href="${pageContext.request.contextPath}/dashboard" class="nav-item">Dashboard</a>
                        <a href="${pageContext.request.contextPath}/employees" class="nav-item active">Employees</a>
                    </nav>
                </aside>

                <main class="main-content">
                    <header style="margin-bottom: 2rem;">
                        <h1>${not empty employee ? 'Edit Employee' : 'Add New Employee'}</h1>
                        <p style="color: var(--text-muted)">Configure employee profile and assignment.</p>
                    </header>

                    <div class="auth-card" style="max-width: 600px; margin: 0;">
                        <form
                            action="${pageContext.request.contextPath}/employees/${not empty employee ? 'update' : 'insert'}"
                            method="POST" enctype="multipart/form-data">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <c:if test="${not empty employee}">
                                <input type="hidden" name="empId" value="${employee.empId}">
                            </c:if>

                            <div class="form-row">
                                <div class="form-group">
                                    <label for="firstName">First Name</label>
                                    <input type="text" id="firstName" name="firstName" value="${employee.firstName}"
                                        required>
                                </div>
                                <div class="form-group">
                                    <label for="lastName">Last Name</label>
                                    <input type="text" id="lastName" name="lastName" value="${employee.lastName}"
                                        required>
                                </div>
                            </div>

                            <div class="form-row">
                                <div class="form-group">
                                    <label for="email">Email Address</label>
                                    <input type="email" id="email" name="email" value="${employee.email}" required>
                                </div>
                                <div class="form-group">
                                    <label for="phone">Phone Number</label>
                                    <input type="text" id="phone" name="phone" value="${employee.phone}" required>
                                </div>
                            </div>

                            <div class="form-row">
                                <div class="form-group">
                                    <label for="deptId">Department</label>
                                    <select name="deptId" id="deptId" required
                                        style="width: 100%; padding: 0.625rem; border: 1px solid var(--border); border-radius: 0.5rem;">
                                        <option value="">Select Department</option>
                                        <c:forEach var="dept" items="${departments}">
                                            <option value="${dept.deptId}" ${employee.deptId==dept.deptId ? 'selected'
                                                : '' }>
                                                ${dept.deptName}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label for="jobTitle">Job Title</label>
                                    <input type="text" id="jobTitle" name="jobTitle" value="${employee.jobTitle}"
                                        required>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="salary">Salary</label>
                                <input type="number" step="0.01" id="salary" name="salary" value="${employee.salary}"
                                    required>
                            </div>

                            <div class="form-group">
                                <label for="profilePic">Profile Picture</label>
                                <input type="file" id="profilePic" name="profilePic" accept="image/*">
                                <c:if test="${not empty employee.profilePicPath}">
                                    <p style="margin-top: 0.5rem; font-size: 0.75rem;">Current:
                                        ${employee.profilePicPath}</p>
                                </c:if>
                            </div>

                            <div style="display: flex; gap: 1rem; margin-top: 2rem;">
                                <button type="submit" class="btn btn-primary">${not empty employee ? 'Update Employee' :
                                    'Create Employee'}</button>
                                <a href="${pageContext.request.contextPath}/employees" class="btn"
                                    style="background: #E5E7EB;">Cancel</a>
                            </div>
                        </form>
                    </div>
                </main>
            </div>
        </body>

        </html>