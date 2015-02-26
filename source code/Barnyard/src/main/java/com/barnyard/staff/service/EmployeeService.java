package com.barnyard.staff.service;

import com.barnyard.staff.domain.Employee;

import java.util.Collection;

public interface EmployeeService {
    Employee getEmployee(long id);

    Collection<Employee> getAllEmployees();

    Collection<Employee> findEmployees(String term);

    void saveEmployee(Employee employee);
}
