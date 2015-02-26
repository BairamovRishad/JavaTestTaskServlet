package com.barnyard.staff.service;

import com.barnyard.staff.domain.Department;

import java.util.Collection;

public interface DepartmentService {
    Department getDepartment(long id);

    Collection<Department> getAllDepartments();

    Collection<Department> findDepartments(String name);

    void saveDepartment(Department department);
}
