package com.barnyard.staff.service;

import com.barnyard.staff.dao.EmployeeRepository;
import com.barnyard.staff.domain.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service("employeeService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Employee getEmployee(long id) {
        try {
            Employee employee = employeeRepository.get(id);
            return employee;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("employeeNotFound.message");
        }
    }

    @Override
    public Collection<Employee> getAllEmployees() {
        return employeeRepository.getAll();
    }

    @Override
    public Collection<Employee> findEmployees(String term) {
        return employeeRepository.findEmployees(term);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void saveEmployee(Employee employee) {
        employeeRepository.save(employee);
    }

}
