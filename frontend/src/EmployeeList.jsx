import React, { useState, useEffect } from 'react';

function EmployeeList() {
    const [employees, setEmployees] = useState([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [loading, setLoading] = useState(false);
    const size = 5;

    useEffect(() => {
        fetchEmployees(page);
    }, [page]);

    const fetchEmployees = async (pageNumber) => {
        setLoading(true);
        try {
            const response = await fetch(`/api/employees/paged?page=${pageNumber}&size=${size}`);
            if (response.ok) {
                const data = await response.json();
                setEmployees(data.content);
                setTotalPages(data.totalPages);
            } else {
                console.error("Failed to fetch employees");
            }
        } catch (error) {
            console.error("Error fetching employees:", error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{ padding: '20px', border: '1px solid #ccc', margin: '10px', borderRadius: '8px' }}>
            <h2>Employee List (Frontend API 1 with Pagination)</h2>
            {loading ? <p>Loading...</p> : (
                <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                    <thead>
                        <tr>
                            <th style={{ borderBottom: '1px solid #ccc', padding: '8px' }}>ID</th>
                            <th style={{ borderBottom: '1px solid #ccc', padding: '8px' }}>First Name</th>
                            <th style={{ borderBottom: '1px solid #ccc', padding: '8px' }}>Last Name</th>
                            <th style={{ borderBottom: '1px solid #ccc', padding: '8px' }}>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        {employees.map(emp => (
                            <tr key={emp.empId}>
                                <td style={{ padding: '8px', textAlign: 'center' }}>{emp.empId}</td>
                                <td style={{ padding: '8px', textAlign: 'center' }}>{emp.firstName}</td>
                                <td style={{ padding: '8px', textAlign: 'center' }}>{emp.lastName}</td>
                                <td style={{ padding: '8px', textAlign: 'center' }}>{emp.status}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
            
            <div style={{ marginTop: '15px', display: 'flex', justifyContent: 'center', gap: '10px' }}>
                <button 
                    disabled={page === 0} 
                    onClick={() => setPage(page - 1)}
                    style={{ padding: '5px 10px', cursor: page === 0 ? 'not-allowed' : 'pointer' }}
                >
                    Previous
                </button>
                <span>Page {page + 1} of {totalPages}</span>
                <button 
                    disabled={page >= totalPages - 1} 
                    onClick={() => setPage(page + 1)}
                    style={{ padding: '5px 10px', cursor: page >= totalPages - 1 ? 'not-allowed' : 'pointer' }}
                >
                    Next
                </button>
            </div>
        </div>
    );
}

export default EmployeeList;
