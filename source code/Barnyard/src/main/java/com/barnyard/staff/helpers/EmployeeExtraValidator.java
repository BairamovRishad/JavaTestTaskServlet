package com.barnyard.staff.helpers;

import com.barnyard.staff.domain.Employee;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.sql.Date;

@Component
public class EmployeeExtraValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Employee.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Employee emp = (Employee) target;
        if (emp.getBirthdate() != null && emp.getBirthdate().before(Date.valueOf("1900-01-01"))) {
            errors.rejectValue("birthdate", "invalid.outOfRangeBirthdate");
        }
    }
}
