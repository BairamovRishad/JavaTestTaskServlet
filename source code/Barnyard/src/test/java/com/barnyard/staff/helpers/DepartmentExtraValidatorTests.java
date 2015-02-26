package com.barnyard.staff.helpers;

import com.barnyard.staff.domain.Department;
import com.barnyard.staff.service.DepartmentService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test.xml")
public class DepartmentExtraValidatorTests {

    DepartmentExtraValidator departmentExtraValidator;

    @Autowired
    private DepartmentService departmentServiceMock;

    @Before
    public void setUp() {
        reset(departmentServiceMock);

        departmentExtraValidator = new DepartmentExtraValidator(departmentServiceMock);
    }

    @Test
    public void validate_ValidDepartmentName_ShouldReturnWithNoErrorMessage() {
        String name = "Бухгалтерия";
        Department department = new Department(0, name);
        Errors errorObject = new DirectFieldBindingResult(department, "department");

        when(departmentServiceMock.findDepartments(name)).thenReturn(new ArrayList<Department>());

        departmentExtraValidator.validate(department, errorObject);

        verify(departmentServiceMock, times(1)).findDepartments(name);
        assertFalse(errorObject.hasErrors());
    }

    @Test
    public void validate_ExistentDepartmentName_ShouldReturnWithErrorMessage() {
        String name = "Бухгалтерия";
        Department department = new Department(0, name);
        Errors errorObject = new DirectFieldBindingResult(department, "department");

        when(departmentServiceMock.findDepartments(name)).thenReturn(Arrays.asList(department));

        departmentExtraValidator.validate(department, errorObject);

        verify(departmentServiceMock, times(1)).findDepartments(name);
        assertTrue(errorObject.hasFieldErrors("name"));
        assertEquals(1, errorObject.getFieldErrorCount("name"));
    }

    @Test
    public void validate_InvalidDepartmentName_ShouldReturnWithErrorMessage() {
        String name = "Бухгалтерия";
        Department department = new Department(0, name);
        DirectFieldBindingResult errorObject = new DirectFieldBindingResult(department, "department");
        errorObject.addError(new FieldError("department", "name", "Invalid name"));

        departmentExtraValidator.validate(department, errorObject);

        verify(departmentServiceMock, times(0)).findDepartments(name);
        assertTrue(errorObject.hasFieldErrors("name"));
        assertEquals(1, errorObject.getFieldErrorCount("name"));
    }

}
