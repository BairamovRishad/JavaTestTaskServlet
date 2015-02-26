package com.barnyard.staff.dao;

import com.barnyard.staff.domain.Employee;

import java.util.Collection;

public interface EmployeeRepository {
    Employee get(long id);

    Collection<Employee> getAll();

    Collection<Employee> findEmployees(String term);

    void save(Employee employee);
}
