import React, { useState, useEffect } from 'react';

function DepartmentList() {
    const [departments, setDepartments] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        fetchDepartments();
    }, []);

    const fetchDepartments = async () => {
        setLoading(true);
        try {
            const response = await fetch('/api/departments');
            if (response.ok) {
                const data = await response.json();
                setDepartments(data);
            } else {
                console.error("Failed to fetch departments");
            }
        } catch (error) {
            console.error("Error fetching departments:", error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{ padding: '20px', border: '1px solid #ccc', margin: '10px', borderRadius: '8px' }}>
            <h2>Department List (Frontend API 2)</h2>
            {loading ? <p>Loading...</p> : (
                <ul style={{ listStyleType: 'none', padding: 0 }}>
                    {departments.map(dept => (
                        <li key={dept.deptId} style={{ padding: '10px', borderBottom: '1px solid #eee' }}>
                            <strong>{dept.deptName}</strong> (ID: {dept.deptId})
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}

export default DepartmentList;
