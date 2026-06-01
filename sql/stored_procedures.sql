-- ── StaffEase Stored Procedures / Functions ──────────────────────────────
-- Run this against your ems_db PostgreSQL database.

-- 1. calculate_annual_bonus
--    Returns the bonus amount for an employee given a bonus percentage.
--    Called by EmployeeRepository.calculateAnnualBonus()
CREATE OR REPLACE FUNCTION calculate_annual_bonus(
    p_emp_id    INT,
    p_bonus_pct NUMERIC DEFAULT 10
)
RETURNS NUMERIC AS $$
DECLARE
    v_salary NUMERIC;
BEGIN
    SELECT salary INTO v_salary
    FROM   employees
    WHERE  emp_id = p_emp_id;

    IF v_salary IS NULL THEN
        RAISE EXCEPTION 'Employee % not found', p_emp_id;
    END IF;

    RETURN ROUND(v_salary * p_bonus_pct / 100.0, 2);
END;
$$ LANGUAGE plpgsql;


-- 2. get_employee_count_by_dept
--    Returns the number of active employees in a department.
--    Called by DepartmentRepository.getEmployeeCountByDept()
CREATE OR REPLACE FUNCTION get_employee_count_by_dept(p_dept_id INT)
RETURNS INT AS $$
DECLARE
    v_count INT;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM   employees
    WHERE  dept_id = p_dept_id
      AND  status  = 'active';

    RETURN COALESCE(v_count, 0);
END;
$$ LANGUAGE plpgsql;


-- 3. get_department_stats
--    Returns aggregate stats for a department as a result set.
CREATE OR REPLACE FUNCTION get_department_stats(p_dept_id INT)
RETURNS TABLE(
    dept_id         INT,
    dept_name       TEXT,
    employee_count  BIGINT,
    avg_salary      NUMERIC,
    total_payroll   NUMERIC
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        d.dept_id,
        d.dept_name::TEXT,
        COUNT(e.emp_id),
        ROUND(AVG(e.salary), 2),
        ROUND(SUM(e.salary), 2)
    FROM   departments d
    LEFT JOIN employees e ON e.dept_id = d.dept_id AND e.status = 'active'
    WHERE  d.dept_id = p_dept_id
    GROUP BY d.dept_id, d.dept_name;
END;
$$ LANGUAGE plpgsql;
