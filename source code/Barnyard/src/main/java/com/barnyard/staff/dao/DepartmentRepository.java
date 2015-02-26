package com.barnyard.staff.dao;

import com.barnyard.staff.domain.Department;

import java.util.Collection;

public interface DepartmentRepository {
    Department get(long id);

    Collection<Department> getAll();

    Collection<Department> findDepartments(String name);

    void save(Department employee);
}
