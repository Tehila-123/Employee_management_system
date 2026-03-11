<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <title>Request Leave - EMS</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
        </head>

        <body>
            <div class="dashboard-container">
                <aside class="sidebar">
                    <div class="sidebar-header">EMS PRO</div>
                    <nav>
                        <a href="${pageContext.request.contextPath}/dashboard" class="nav-item">Dashboard</a>
                        <a href="${pageContext.request.contextPath}/leave" class="nav-item active">Leave Requests</a>
                    </nav>
                </aside>

                <main class="main-content">
                    <h1>Request Leave</h1>
                    <p style="color: var(--text-muted); margin-bottom: 2rem;">Submit a new leave application.</p>

                    <div class="auth-card" style="max-width: 500px; margin:0;">
                        <form action="${pageContext.request.contextPath}/leave/apply" method="POST">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                            <div class="form-group">
                                <label for="leaveType">Leave Type</label>
                                <select name="leaveType" id="leaveType" required class="form-group"
                                    style="width: 100%; padding: 0.625rem; border: 1px solid var(--border); border-radius: 0.5rem;">
                                    <option value="Annual">Annual</option>
                                    <option value="Sick">Sick</option>
                                    <option value="Personal">Personal</option>
                                </select>
                            </div>

                            <div class="form-row">
                                <div class="form-group">
                                    <label for="startDate">Start Date</label>
                                    <input type="date" id="startDate" name="startDate" required>
                                </div>
                                <div class="form-group">
                                    <label for="endDate">End Date</label>
                                    <input type="date" id="endDate" name="endDate" required>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="reason">Reason</label>
                                <textarea id="reason" name="reason" rows="4" required
                                    style="width: 100%; border: 1px solid var(--border); border-radius: 0.5rem; padding: 0.5rem;"></textarea>
                            </div>

                            <div style="display: flex; gap: 1rem; margin-top: 1rem;">
                                <button type="submit" class="btn btn-primary">Submit Request</button>
                                <a href="${pageContext.request.contextPath}/leave" class="btn"
                                    style="background: #E5E7EB;">Cancel</a>
                            </div>
                        </form>
                    </div>
                </main>
            </div>
        </body>

        </html>
