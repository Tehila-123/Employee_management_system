package com.ems.dto;

/**
 * DTO returned by the annual bonus calculation stored procedure endpoint.
 */
public class BonusResponse {

    private int empId;
    private String employeeName;
    private double baseSalary;
    private double bonusAmount;
    private double annualTotal;

    public BonusResponse() {}

    public BonusResponse(int empId, String employeeName, double baseSalary, double bonusAmount) {
        this.empId = empId;
        this.employeeName = employeeName;
        this.baseSalary = baseSalary;
        this.bonusAmount = bonusAmount;
        this.annualTotal = (baseSalary * 12) + bonusAmount;
    }

    public int getEmpId() { return empId; }
    public void setEmpId(int empId) { this.empId = empId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public double getBaseSalary() { return baseSalary; }
    public void setBaseSalary(double baseSalary) { this.baseSalary = baseSalary; }

    public double getBonusAmount() { return bonusAmount; }
    public void setBonusAmount(double bonusAmount) { this.bonusAmount = bonusAmount; }

    public double getAnnualTotal() { return annualTotal; }
    public void setAnnualTotal(double annualTotal) { this.annualTotal = annualTotal; }
}
