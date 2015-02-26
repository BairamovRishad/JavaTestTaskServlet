package com.barnyard.staff.service;

import com.barnyard.staff.dao.DepartmentRepository;
import com.barnyard.staff.domain.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service("departmentService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class DepartmentServiceImpl implements DepartmentService {

    DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Department getDepartment(long id) {
        try {
            Department department = departmentRepository.get(id);
            return department;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("departmentNotFound.message");
        }
    }

    @Override
    public Collection<Department> getAllDepartments() {
        return departmentRepository.getAll();
    }

    @Override
    public Collection<Department> findDepartments(String name) {
        return departmentRepository.findDepartments(name);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void saveDepartment(Department department) {
        departmentRepository.save(department);
    }

}