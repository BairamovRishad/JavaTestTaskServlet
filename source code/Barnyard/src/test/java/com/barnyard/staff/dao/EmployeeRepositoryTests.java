package com.barnyard.staff.dao;

import com.barnyard.staff.domain.Employee;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:persistence.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class EmployeeRepositoryTests {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    public void get() throws ParseException {
        Date birthdate = DateFormat.getDateInstance().parse("02.02.1980");
        Employee expected = new Employee.EmployeeBuilder().id(1L).departmentId(1).firstName("Василий").lastName("Пупкин")
                .salary(12000.0).birthdate(birthdate).active(true).build();

        Employee actual = employeeRepository.get(1L);

        assertEquals(expected, actual);
    }

    @Test
    public void getAll() throws ParseException {
        // Assign
        Date firstBirthdate = DateFormat.getDateInstance().parse("02.02.1980");
        Employee first = new Employee.EmployeeBuilder().id(1L).departmentId(1).firstName("Василий").lastName("Пупкин")
                .salary(12000.0).birthdate(firstBirthdate).active(true).build();

        Date secondBirthdate = DateFormat.getDateInstance().parse("03.03.1960");
        Employee second = new Employee.EmployeeBuilder().id(2L).departmentId(1).firstName("Елена").lastName("Павлова")
                .salary(15000.0).birthdate(secondBirthdate).active(true).build();

        Collection<Employee> expected = Arrays.asList(first, second);

        // Act
        Collection<Employee> actual = employeeRepository.getAll();

        // Assert
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void findDepartments() throws ParseException {
        Date firstBirthdate = DateFormat.getDateInstance().parse("02.02.1980");
        Employee expected = new Employee.EmployeeBuilder().id(1L).departmentId(1).firstName("Василий")
                .lastName("Пупкин").salary(12000.0).birthdate(firstBirthdate).active(true).build();

        String[] terms = {"ПупкинВасилий", "пупкинвасилий", "ПУПКИНВАСИЛИЙ", "пу*", "??пкин*", "*асилий"};

        for (String term : terms) {
            Collection<Employee> actual = employeeRepository.findEmployees(term);

            assertEquals(1, actual.size());
            assertTrue(actual.contains(expected));
        }
    }

    @Test
    public void save_create() throws ParseException {
        // Assign
        Date birthdate = DateFormat.getDateInstance().parse("05.05.1986");
        Employee employee = new Employee.EmployeeBuilder().departmentId(1).firstName("Василиса").lastName("Пупкина")
                .salary(12000.0).birthdate(birthdate).active(true).build();

        int before = employeeRepository.getAll().size();

        // Act
        employeeRepository.save(employee);

        // Assert
        int after = employeeRepository.getAll().size();
        assertEquals(before + 1, after);
    }

    @Test
    public void save_update() throws ParseException {
        // Assign
        long id = 1L;
        Date birthdate = DateFormat.getDateInstance().parse("05.05.1986");
        Employee updatedEmployee = new Employee.EmployeeBuilder().id(id).departmentId(1).firstName("Василиса").lastName("Пупкина")
                .salary(12000.0).birthdate(birthdate).active(true).build();

        // Act
        employeeRepository.save(updatedEmployee);

        // Assert
        Employee actual = employeeRepository.get(id);
        assertEquals(updatedEmployee, actual);
    }

}
