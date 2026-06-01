<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <title>Dashboard - EMS</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
            <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
            <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
        </head>

        <body>
            <div class="dashboard-container">
                <!-- Sidebar -->
                <aside class="sidebar">
                    <div class="sidebar-header">StaffEase</div>
                    <nav>
                        <a href="${pageContext.request.contextPath}/dashboard" class="nav-item active">Dashboard</a>
                        <a href="${pageContext.request.contextPath}/employees" class="nav-item">Employees</a>
                        <a href="${pageContext.request.contextPath}/departments" class="nav-item">Departments</a>
                        <c:if test="${sessionScope.user.roleName == 'Admin'}">
                            <a href="${pageContext.request.contextPath}/admin/audit-logs" class="nav-item">Audit
                                Logs</a>
                        </c:if>
                        <div style="margin-top: 2rem;">
                            <a href="${pageContext.request.contextPath}/logout" class="nav-item"
                                style="color: #F87171;">Logout</a>
                        </div>
                    </nav>
                </aside>

                <!-- Main Content -->
                <main class="main-content">
                    <header style="margin-bottom: 2rem;">
                        <h1>Welcome, ${sessionScope.user.email}</h1>
                        <p style="color: var(--text-muted)">Here's what's happening today.</p>
                    </header>

                    <div class="stats-grid">
                        <div class="stat-card">
                            <h3>Total Employees</h3>
                            <div class="value" id="totalEmployeesValue">${totalEmployees}</div>
                        </div>
                        <div class="stat-card">
                            <h3>Active Employees</h3>
                            <div class="value" id="activeEmployeesValue">${activeEmployees}</div>
                        </div>

                    </div>

                    <div class="charts-grid">
                        <div class="chart-container">
                            <h3>Employees per Department</h3>
                            <div style="position: relative; height: 300px; width: 100%;">
                                <canvas id="deptChart"></canvas>
                            </div>
                        </div>
                        <div class="chart-container">
                            <h3>Average Salary per Department</h3>
                            <div style="position: relative; height: 300px; width: 100%;">
                                <canvas id="salaryChart"></canvas>
                            </div>
                        </div>
                    </div>
                </main>
            </div>

            <script>
                // Initial variables for charts reference
                let deptChart, salaryChart;

                // Auto-update function
                function updateDashboard() {
                    console.log('Fetching dashboard updates...');
                    fetch('${pageContext.request.contextPath}/dashboard', {
                        headers: {
                            'X-Requested-With': 'XMLHttpRequest'
                        }
                    })
                    .then(response => {
                        if (!response.ok) throw new Error('Network response was not ok');
                        return response.json();
                    })
                    .then(data => {
                        console.log('Dashboard data received:', data);
                        document.getElementById('totalEmployeesValue').innerText = data.totalEmployees;
                        document.getElementById('activeEmployeesValue').innerText = data.activeEmployees;
                        
                        // Update charts if data changed
                        if (deptChart && data.deptLabels && data.deptData) {
                            deptChart.data.labels = data.deptLabels;
                            deptChart.data.datasets[0].data = data.deptData;
                            deptChart.update();
                        }
                        
                        if (salaryChart && data.salaryLabels && data.salaryData) {
                            salaryChart.data.labels = data.salaryLabels;
                            salaryChart.data.datasets[0].data = data.salaryData;
                            salaryChart.update();
                        }
                    })
                    .catch(error => console.error('Error updating dashboard:', error));
                }
                
                // Redefine chart creation to store in variables
                window.onload = function() {
                    const deptCtx = document.getElementById('deptChart').getContext('2d');
                    const salaryCtx = document.getElementById('salaryChart').getContext('2d');

                    deptChart = new Chart(deptCtx, {
                        type: 'doughnut',
                        data: {
                            labels: ${deptLabels},
                            datasets: [{
                                data: ${deptData},
                                backgroundColor: ['#4F46E5', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6', '#EC4899']
                            }]
                        },
                        options: {
                            responsive: true,
                            maintainAspectRatio: false,
                            plugins: {
                                legend: {
                                    position: 'bottom'
                                }
                            }
                        }
                    });

                    salaryChart = new Chart(salaryCtx, {
                        type: 'bar',
                        data: {
                            labels: ${salaryLabels},
                            datasets: [{
                                label: 'Avg Salary',
                                data: ${salaryData},
                                backgroundColor: '#10B981',
                                borderRadius: 4
                            }]
                        },
                        options: {
                            responsive: true,
                            maintainAspectRatio: false,
                            scales: {
                                y: {
                                    beginAtZero: true
                                }
                            },
                            plugins: {
                                legend: {
                                    display: false
                                }
                            }
                        }
                    });

                    // Start polling every 30 seconds
                    setInterval(updateDashboard, 30000);
                };
            </script>
        </body>

        </html>