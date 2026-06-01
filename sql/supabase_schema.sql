-- StaffEase Supabase Schema
-- Run this in your Supabase SQL editor

-- Enable UUID extension
create extension if not exists "uuid-ossp";

-- ── Enums ──────────────────────────────────────────────────────────────────
create type user_role as enum ('super_admin', 'manager', 'employee');
create type employee_status as enum ('active', 'onboarding', 'inactive');
create type work_mode as enum ('Remote', 'In-office', 'Hybrid');
create type leave_type as enum ('PTO', 'Sick Leave', 'Maternity/Paternity', 'Unpaid Leave');
create type leave_status as enum ('pending', 'approved', 'denied');
create type doc_type as enum ('contract', 'payslip', 'id', 'other');
create type skill_level as enum ('1','2','3','4','5');

-- ── Departments ────────────────────────────────────────────────────────────
create table departments (
  id uuid primary key default uuid_generate_v4(),
  name text not null unique,
  created_at timestamptz default now()
);

-- ── Employees ──────────────────────────────────────────────────────────────
create table employees (
  id uuid primary key default uuid_generate_v4(),
  auth_user_id uuid references auth.users(id) on delete set null,
  first_name text not null,
  last_name text not null,
  email text not null unique,
  title text,
  department_id uuid references departments(id) on delete set null,
  role user_role not null default 'employee',
  status employee_status not null default 'onboarding',
  work_mode work_mode not null default 'Remote',
  hire_date date not null default current_date,
  birthday date,
  manager_id uuid references employees(id) on delete set null,
  created_at timestamptz default now(),
  updated_at timestamptz default now()
);

-- ── Skills ─────────────────────────────────────────────────────────────────
create table skills (
  id uuid primary key default uuid_generate_v4(),
  name text not null unique
);

create table employee_skills (
  id uuid primary key default uuid_generate_v4(),
  employee_id uuid not null references employees(id) on delete cascade,
  skill_id uuid not null references skills(id) on delete cascade,
  level integer not null check (level between 1 and 5),
  unique(employee_id, skill_id)
);

-- ── Leave Requests ─────────────────────────────────────────────────────────
create table leave_requests (
  id uuid primary key default uuid_generate_v4(),
  employee_id uuid not null references employees(id) on delete cascade,
  type leave_type not null,
  start_date date not null,
  end_date date not null,
  days integer generated always as (end_date - start_date + 1) stored,
  reason text,
  status leave_status not null default 'pending',
  reviewed_by uuid references employees(id) on delete set null,
  reviewed_at timestamptz,
  submitted_at timestamptz default now(),
  constraint valid_dates check (end_date >= start_date)
);

-- ── Documents ──────────────────────────────────────────────────────────────
create table documents (
  id uuid primary key default uuid_generate_v4(),
  employee_id uuid not null references employees(id) on delete cascade,
  name text not null,
  type doc_type not null default 'other',
  storage_path text not null,  -- Supabase Storage path
  size_bytes bigint,
  uploaded_by uuid references employees(id) on delete set null,
  uploaded_at timestamptz default now()
);

-- ── Onboarding Checklists ──────────────────────────────────────────────────
create table onboarding_tasks (
  id uuid primary key default uuid_generate_v4(),
  employee_id uuid not null references employees(id) on delete cascade,
  task text not null,
  category text not null,
  due_day integer not null default 1,
  completed boolean not null default false,
  completed_at timestamptz,
  created_at timestamptz default now()
);

-- ── Goals / OKRs ───────────────────────────────────────────────────────────
create table goals (
  id uuid primary key default uuid_generate_v4(),
  employee_id uuid not null references employees(id) on delete cascade,
  title text not null,
  key_result text,
  progress integer not null default 0 check (progress between 0 and 100),
  due_date date,
  created_at timestamptz default now(),
  updated_at timestamptz default now()
);

-- ── Audit Log ──────────────────────────────────────────────────────────────
create table audit_logs (
  id uuid primary key default uuid_generate_v4(),
  actor_id uuid references employees(id) on delete set null,
  action text not null,
  table_name text,
  record_id uuid,
  old_data jsonb,
  new_data jsonb,
  created_at timestamptz default now()
);

-- ── Row Level Security ─────────────────────────────────────────────────────
alter table employees enable row level security;
alter table leave_requests enable row level security;
alter table documents enable row level security;
alter table onboarding_tasks enable row level security;
alter table goals enable row level security;

-- Employees: super_admin sees all, manager sees their team, employee sees self
create policy "employees_select" on employees for select using (
  auth.uid() in (
    select auth_user_id from employees where role = 'super_admin'
  )
  or auth.uid() = auth_user_id
  or auth.uid() in (
    select e.auth_user_id from employees e where e.id = employees.manager_id
  )
);

-- Leave: employee sees own, manager sees team, admin sees all
create policy "leave_select" on leave_requests for select using (
  auth.uid() in (select auth_user_id from employees where role = 'super_admin')
  or auth.uid() = (select auth_user_id from employees where id = leave_requests.employee_id)
  or auth.uid() in (
    select e.auth_user_id from employees e
    join employees sub on sub.manager_id = e.id
    where sub.id = leave_requests.employee_id
  )
);

-- Documents: employee sees own, admin sees all
create policy "documents_select" on documents for select using (
  auth.uid() in (select auth_user_id from employees where role = 'super_admin')
  or auth.uid() = (select auth_user_id from employees where id = documents.employee_id)
);

-- ── Trigger: auto-create onboarding checklist ──────────────────────────────
create or replace function create_onboarding_tasks()
returns trigger language plpgsql as $$
begin
  if new.status = 'onboarding' then
    insert into onboarding_tasks (employee_id, task, category, due_day) values
      (new.id, 'Sign Employment Contract', 'Legal', 1),
      (new.id, 'Complete Tax Forms', 'Legal', 1),
      (new.id, 'IT Setup — Laptop & Accounts', 'Technical', 1),
      (new.id, 'Set up Dev Environment', 'Technical', 3),
      (new.id, 'Intro Meeting with Manager', 'People', 1),
      (new.id, 'Meet the Team (Virtual Coffee)', 'People', 3),
      (new.id, 'Complete Security Training', 'Compliance', 5),
      (new.id, 'Review Company Handbook', 'Compliance', 5);
  end if;
  return new;
end;
$$;

create trigger on_employee_onboarding
  after insert on employees
  for each row execute function create_onboarding_tasks();

-- ── Trigger: update leave status → mark calendar ───────────────────────────
-- (In a real app you'd notify a calendar service here via pg_notify or a webhook)
create or replace function notify_leave_approved()
returns trigger language plpgsql as $$
begin
  if new.status = 'approved' and old.status = 'pending' then
    -- Insert audit log entry
    insert into audit_logs (action, table_name, record_id, old_data, new_data)
    values ('leave_approved', 'leave_requests', new.id,
      jsonb_build_object('status', old.status),
      jsonb_build_object('status', new.status, 'start_date', new.start_date, 'end_date', new.end_date)
    );
  end if;
  return new;
end;
$$;

create trigger on_leave_approved
  after update on leave_requests
  for each row execute function notify_leave_approved();
