package com.ems.dto;

/**
 * DTO for returning employee data to the API consumer.
 * Keeps the entity internal and controls what fields are exposed.
 */
public class EmployeeResponse {

    private int empId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String jobTitle;
    private Integer deptId;
    private String deptName;
    private String status;
    private double salary;
    private String hireDate;

    // ── Getters & Setters ──────────────────────────────────────────────────

    public int getEmpId() { return empId; }
    public void setEmpId(int empId) { this.empId = empId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public Integer getDeptId() { return deptId; }
    public void setDeptId(Integer deptId) { this.deptId = deptId; }

    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    public String getHireDate() { return hireDate; }
    public void setHireDate(String hireDate) { this.hireDate = hireDate; }
}
