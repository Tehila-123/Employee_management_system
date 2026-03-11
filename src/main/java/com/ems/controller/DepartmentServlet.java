package com.ems.controller;

import com.ems.dao.DepartmentDAO;
import com.ems.dao.EmployeeDAOImpl;
import com.ems.model.Department;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/departments/*")
public class DepartmentServlet extends HttpServlet {
    private DepartmentDAO departmentDAO;
    private EmployeeDAOImpl employeeDAO;

    @Override
    public void init() throws ServletException {
        departmentDAO = new DepartmentDAO();
        employeeDAO = new EmployeeDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getPathInfo();
        try {
            if (action == null || action.equals("/")) {
                listDepartments(req, resp);
            } else if (action.equals("/new")) {
                showNewForm(req, resp);
            } else if (action.equals("/edit")) {
                showEditForm(req, resp);
            } else if (action.equals("/delete")) {
                deleteDepartment(req, resp);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void listDepartments(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        req.setAttribute("departments", departmentDAO.getAllDepartments());
        req.getRequestDispatcher("/WEB-INF/jsp/department-list.jsp").forward(req, resp);
    }

    private void showNewForm(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        req.setAttribute("employees", employeeDAO.getAllEmployees(0, 500));
        req.getRequestDispatcher("/WEB-INF/jsp/department-form.jsp").forward(req, resp);
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        departmentDAO.getDepartmentById(id).ifPresent(dept -> req.setAttribute("department", dept));
        req.setAttribute("employees", employeeDAO.getAllEmployees(0, 500));
        req.getRequestDispatcher("/WEB-INF/jsp/department-form.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getPathInfo();
        try {
            if (action.equals("/insert")) {
                insertDepartment(req, resp);
            } else if (action.equals("/update")) {
                updateDepartment(req, resp);
            } else if (action.equals("/delete")) {
                deleteDepartment(req, resp);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void insertDepartment(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        String name = req.getParameter("deptName");
        int managerId = req.getParameter("managerId") != null ? Integer.parseInt(req.getParameter("managerId")) : 0;
        Department dept = new Department();
        dept.setDeptName(name);
        dept.setManagerId(managerId);
        departmentDAO.createDepartment(dept);
        resp.sendRedirect(req.getContextPath() + "/departments");
    }

    private void updateDepartment(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        int id = Integer.parseInt(req.getParameter("deptId"));
        String name = req.getParameter("deptName");
        int managerId = req.getParameter("managerId") != null ? Integer.parseInt(req.getParameter("managerId")) : 0;
        Department dept = new Department();
        dept.setDeptId(id);
        dept.setDeptName(name);
        dept.setManagerId(managerId);
        departmentDAO.updateDepartment(dept);
        resp.sendRedirect(req.getContextPath() + "/departments");
    }

    private void deleteDepartment(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        try {
            departmentDAO.deleteDepartment(id);
        } catch (SQLException e) {
            // Usually foreign key constraint violation indicating employees are still there
            System.err.println("Cannot delete department " + id + ": employees exist.");
            // Optionally set an error attribute and forward, but redirecting is okay for a simple fix
            // req.getSession().setAttribute("error", "Cannot delete department with active employees.");
        }
        resp.sendRedirect(req.getContextPath() + "/departments");
    }
}

