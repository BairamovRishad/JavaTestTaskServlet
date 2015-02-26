package com.barnyard.staff.helpers;

import com.barnyard.staff.domain.Department;
import com.barnyard.staff.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class DepartmentExtraValidator implements Validator {

    DepartmentService departmentService;

    @Autowired
    public DepartmentExtraValidator(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Department.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Department department = (Department) target;

        if (!errors.hasErrors() && !departmentService.findDepartments(department.getName()).isEmpty()) {
            errors.rejectValue("name", "invalid.existingDepartment");
        }
    }
}
