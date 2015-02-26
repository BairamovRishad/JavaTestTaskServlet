package com.barnyard.staff.helpers;

import com.barnyard.staff.domain.Employee;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Errors;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test.xml")
public class EmployeeExtraValidatorTests {

    EmployeeExtraValidator employeeExtraValidator;

    @Before
    public void setUp() {
        employeeExtraValidator = new EmployeeExtraValidator();
    }

    @Test
    public void validate_ValidEmployeeBirthdate_ShouldReturnWithNoErrorMessage() throws ParseException {
        Date birthdate = DateFormat.getDateInstance().parse("01.01.2010");
        Employee employee = new Employee.EmployeeBuilder().birthdate(birthdate).build();
        Errors errorObject = new DirectFieldBindingResult(employee, "employee");

        employeeExtraValidator.validate(employee, errorObject);

        assertFalse(errorObject.hasErrors());
    }

    @Test
    public void validate_InvalidEmployeeBirthdate_ShouldReturnWithErrorMessage() throws ParseException {
        Date birthdate = DateFormat.getDateInstance().parse("01.01.1010");

        Employee employee = new Employee.EmployeeBuilder().birthdate(birthdate).build();
        Errors errorObject = new DirectFieldBindingResult(employee, "employee");

        employeeExtraValidator.validate(employee, errorObject);

        assertTrue(errorObject.hasFieldErrors("birthdate"));
    }

}
