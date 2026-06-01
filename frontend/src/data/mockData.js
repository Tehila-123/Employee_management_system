export const CURRENT_USER = {
  id: '1',
  name: 'Tehila Ruzindana',
  email: 'tehilaruzindana@gmail.com',
  role: 'super_admin',
  avatar: null,
  department: 'Engineering',
  title: 'Super Admin',
};

export const EMPLOYEES = [
  { id: '1', name: 'Alex Rivera', email: 'alex@staffease.io', role: 'super_admin', title: 'Founder & CTO', department: 'Engineering', status: 'active', workMode: 'Remote', hireDate: '2022-01-10', birthday: '1990-06-15', skills: [{ name: 'React', level: 5 }, { name: 'Node.js', level: 4 }, { name: 'Leadership', level: 5 }], avatar: null },
  { id: '2', name: 'Jordan Kim', email: 'jordan@staffease.io', role: 'manager', title: 'Engineering Manager', department: 'Engineering', status: 'active', workMode: 'Remote', hireDate: '2022-03-01', birthday: '1988-11-22', skills: [{ name: 'Python', level: 5 }, { name: 'AWS', level: 4 }, { name: 'Mentoring', level: 4 }], avatar: null },
  { id: '3', name: 'Sam Patel', email: 'sam@staffease.io', role: 'employee', title: 'Senior Frontend Dev', department: 'Engineering', status: 'active', workMode: 'In-office', hireDate: '2022-06-15', birthday: '1993-03-08', skills: [{ name: 'Vue.js', level: 5 }, { name: 'TypeScript', level: 4 }, { name: 'CSS', level: 5 }], avatar: null },
  { id: '4', name: 'Morgan Lee', email: 'morgan@staffease.io', role: 'employee', title: 'Product Designer', department: 'Design', status: 'onboarding', workMode: 'Remote', hireDate: '2026-05-20', birthday: '1995-08-30', skills: [{ name: 'Figma', level: 5 }, { name: 'UX Research', level: 3 }], avatar: null },
  { id: '5', name: 'Casey Brown', email: 'casey@staffease.io', role: 'employee', title: 'Backend Engineer', department: 'Engineering', status: 'active', workMode: 'Remote', hireDate: '2023-02-01', birthday: '1991-12-05', skills: [{ name: 'Java', level: 5 }, { name: 'Spring Boot', level: 5 }, { name: 'PostgreSQL', level: 4 }], avatar: null },
  { id: '6', name: 'Riley Chen', email: 'riley@staffease.io', role: 'employee', title: 'Marketing Lead', department: 'Marketing', status: 'active', workMode: 'In-office', hireDate: '2023-04-10', birthday: '1994-05-27', skills: [{ name: 'SEO', level: 4 }, { name: 'Content Strategy', level: 5 }, { name: 'Analytics', level: 3 }], avatar: null },
  { id: '7', name: 'Drew Wilson', email: 'drew@staffease.io', role: 'employee', title: 'DevOps Engineer', department: 'Engineering', status: 'active', workMode: 'Remote', hireDate: '2023-07-01', birthday: '1989-09-14', skills: [{ name: 'Kubernetes', level: 4 }, { name: 'Terraform', level: 4 }, { name: 'CI/CD', level: 5 }], avatar: null },
  { id: '8', name: 'Taylor Nguyen', email: 'taylor@staffease.io', role: 'manager', title: 'Head of Design', department: 'Design', status: 'active', workMode: 'Remote', hireDate: '2022-09-01', birthday: '1987-02-18', skills: [{ name: 'Figma', level: 5 }, { name: 'Brand Strategy', level: 4 }, { name: 'Leadership', level: 4 }], avatar: null },
];

export const LEAVE_REQUESTS = [
  { id: '1', employeeId: '3', employeeName: 'Sam Patel', type: 'PTO', startDate: '2026-06-02', endDate: '2026-06-06', days: 5, reason: 'Family vacation', status: 'pending', submittedAt: '2026-05-25' },
  { id: '2', employeeId: '5', employeeName: 'Casey Brown', type: 'Sick Leave', startDate: '2026-05-28', endDate: '2026-05-28', days: 1, reason: 'Not feeling well', status: 'approved', submittedAt: '2026-05-27' },
  { id: '3', employeeId: '6', employeeName: 'Riley Chen', type: 'PTO', startDate: '2026-06-16', endDate: '2026-06-20', days: 5, reason: 'Personal trip', status: 'pending', submittedAt: '2026-05-26' },
  { id: '4', employeeId: '7', employeeName: 'Drew Wilson', type: 'PTO', startDate: '2026-07-04', endDate: '2026-07-11', days: 6, reason: 'Summer break', status: 'approved', submittedAt: '2026-05-20' },
  { id: '5', employeeId: '3', employeeName: 'Sam Patel', type: 'Maternity/Paternity', startDate: '2026-08-01', endDate: '2026-10-31', days: 65, reason: 'Parental leave', status: 'denied', submittedAt: '2026-05-15' },
];

export const DOCUMENTS = [
  { id: '1', employeeId: '1', name: 'Employment Contract', type: 'contract', uploadedAt: '2022-01-10', size: '245 KB' },
  { id: '2', employeeId: '1', name: 'Payslip - May 2026', type: 'payslip', uploadedAt: '2026-05-01', size: '128 KB' },
  { id: '3', employeeId: '1', name: 'Payslip - Apr 2026', type: 'payslip', uploadedAt: '2026-04-01', size: '128 KB' },
  { id: '4', employeeId: '2', name: 'Employment Contract', type: 'contract', uploadedAt: '2022-03-01', size: '252 KB' },
  { id: '5', employeeId: '3', name: 'NDA Agreement', type: 'contract', uploadedAt: '2022-06-15', size: '98 KB' },
  { id: '6', employeeId: '4', name: 'Offer Letter', type: 'contract', uploadedAt: '2026-05-20', size: '180 KB' },
];

export const ONBOARDING_TEMPLATES = [
  { id: '1', task: 'Sign Employment Contract', category: 'Legal', dueDay: 1 },
  { id: '2', task: 'Complete Tax Forms', category: 'Legal', dueDay: 1 },
  { id: '3', task: 'IT Setup — Laptop & Accounts', category: 'Technical', dueDay: 1 },
  { id: '4', task: 'Set up Dev Environment', category: 'Technical', dueDay: 3 },
  { id: '5', task: 'Intro Meeting with Manager', category: 'People', dueDay: 1 },
  { id: '6', task: 'Meet the Team (Virtual Coffee)', category: 'People', dueDay: 3 },
  { id: '7', task: 'Complete Security Training', category: 'Compliance', dueDay: 5 },
  { id: '8', task: 'Review Company Handbook', category: 'Compliance', dueDay: 5 },
];

export const GOALS = [
  { id: '1', employeeId: '1', title: 'Launch v2.0 of StaffEase', keyResult: 'Ship by Q3 2026', progress: 45, dueDate: '2026-09-30' },
  { id: '2', employeeId: '1', title: 'Grow team to 50 employees', keyResult: 'Hire 12 more people', progress: 30, dueDate: '2026-12-31' },
  { id: '3', employeeId: '3', title: 'Migrate to React 19', keyResult: 'Zero breaking changes', progress: 80, dueDate: '2026-06-30' },
  { id: '4', employeeId: '5', title: 'Reduce API latency by 40%', keyResult: 'P95 < 200ms', progress: 60, dueDate: '2026-07-31' },
];

