package com.ems.controller;

import com.ems.dao.DepartmentDAO;
import com.ems.dao.EmployeeDAOImpl;
import com.ems.model.Employee;
import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/employees/*")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
                 maxFileSize = 1024 * 1024 * 10,      // 10MB
                 maxRequestSize = 1024 * 1024 * 50)   // 50MB
public class EmployeeServlet extends HttpServlet {
    private EmployeeDAOImpl employeeDAO;
    private DepartmentDAO departmentDAO;

    @Override
    public void init() throws ServletException {
        employeeDAO = new EmployeeDAOImpl();
        departmentDAO = new DepartmentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getPathInfo();
        try {
            if (action == null || action.equals("/")) {
                listEmployees(req, resp);
            } else if (action.equals("/new")) {
                showNewForm(req, resp);
            } else if (action.equals("/edit")) {
                showEditForm(req, resp);
            } else if (action.equals("/view")) {
                viewEmployee(req, resp);
            } else if (action.equals("/delete")) {
                deleteEmployee(req, resp);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void listEmployees(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        int page = 1;
        int limit = 10;
        if (req.getParameter("page") != null) page = Integer.parseInt(req.getParameter("page"));
        
        String query = req.getParameter("q");
        List<Employee> list;
        int total;
        
        if (query != null && !query.isEmpty()) {
            list = employeeDAO.searchEmployees(query, (page - 1) * limit, limit);
            total = list.size(); // Simplified total for search
        } else {
            list = employeeDAO.getAllEmployees((page - 1) * limit, limit);
            total = employeeDAO.getTotalEmployeesCount();
        }
        
        req.setAttribute("employees", list);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", (int) Math.ceil((double) total / limit));
        req.getRequestDispatcher("/WEB-INF/jsp/employee-list.jsp").forward(req, resp);
    }

    private void showNewForm(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        req.setAttribute("departments", departmentDAO.getAllDepartments());
        req.getRequestDispatcher("/WEB-INF/jsp/employee-form.jsp").forward(req, resp);
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        employeeDAO.getEmployeeById(id).ifPresent(emp -> req.setAttribute("employee", emp));
        req.setAttribute("departments", departmentDAO.getAllDepartments());
        req.getRequestDispatcher("/WEB-INF/jsp/employee-form.jsp").forward(req, resp);
    }

    private void viewEmployee(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        employeeDAO.getEmployeeById(id).ifPresent(emp -> req.setAttribute("employee", emp));
        req.getRequestDispatcher("/WEB-INF/jsp/employee-view.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getPathInfo();
        try {
            if (action.equals("/insert") || action.equals("/update")) {
                saveEmployee(req, resp);
            } else if (action.equals("/delete")) {
                deleteEmployee(req, resp);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void saveEmployee(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException, ServletException {
        int empId = req.getParameter("empId") != null && !req.getParameter("empId").isEmpty() ? Integer.parseInt(req.getParameter("empId")) : 0;
        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");
        int deptId = Integer.parseInt(req.getParameter("deptId"));
        String jobTitle = req.getParameter("jobTitle");
        String salaryStr = req.getParameter("salary");
        double salary = (salaryStr != null && !salaryStr.isEmpty()) ? Double.parseDouble(salaryStr) : 0.0;
        
        // Handle Profile Picture
        Part filePart = req.getPart("profilePic");
        String profilePicPath = null;
        if (filePart != null && filePart.getSize() > 0) {
            String fileName = System.currentTimeMillis() + "_" + filePart.getSubmittedFileName();
            String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdir();
            filePart.write(uploadPath + File.separator + fileName);
            profilePicPath = "uploads/" + fileName;
        }

        Employee emp = new Employee();
        emp.setFirstName(firstName);
        emp.setLastName(lastName);
        emp.setEmail(email);
        emp.setPhone(phone);
        emp.setDeptId(deptId);
        emp.setJobTitle(jobTitle);
        emp.setSalary(salary);
        if (profilePicPath != null) emp.setProfilePicPath(profilePicPath);

        if (empId == 0) {
            // New employee - hire date today
            emp.setHireDate(new Date(System.currentTimeMillis()));
            
            // Create a new User account for this employee (required 1-to-1 mapping)
            com.ems.model.User newUser = new com.ems.model.User();
            newUser.setEmail(email);
            // Default password is password123. Use SecurityUtil to hash. (Assuming SecurityUtil.hashPassword exists, otherwise we'll fix it)
            newUser.setPasswordHash(com.ems.util.SecurityUtil.hashPassword("password123"));
            newUser.setRoleId(3); // 3 = Employee role as per schema
            
            try {
                com.ems.dao.UserDAOImpl userDAO = new com.ems.dao.UserDAOImpl();
                userDAO.createUser(newUser);
                emp.setUserId(newUser.getUserId());
            } catch (SQLException e) {
                throw new ServletException("Failed to create user account (email might already exist)", e);
            }
            
            employeeDAO.createEmployee(emp);
        } else {
            emp.setEmpId(empId);
            employeeDAO.updateEmployee(emp);
        }
        resp.sendRedirect(req.getContextPath() + "/employees");
    }

    private void deleteEmployee(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        employeeDAO.softDeleteEmployee(id);
        resp.sendRedirect(req.getContextPath() + "/employees");
    }
}

