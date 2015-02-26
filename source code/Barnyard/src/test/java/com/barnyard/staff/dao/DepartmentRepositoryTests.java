package com.barnyard.staff.dao;

import com.barnyard.staff.domain.Department;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:persistence.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class DepartmentRepositoryTests {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Test
    public void get() {
        Department expected = new Department(2, "Бухгалтерия");
        Department actual = departmentRepository.get(2);
        assertEquals(expected, actual);
    }

    @Test
    public void getAll() {
        Department first = new Department(1, "Не определено");
        Department second = new Department(2, "Бухгалтерия");
        Collection<Department> expected = Arrays.asList(first, second);

        Collection<Department> actual = departmentRepository.getAll();

        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void findDepartments() {
        Department expected = new Department(2, "Бухгалтерия");
        Collection<Department> actual = departmentRepository.findDepartments("Бухгалтерия");

        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void save_create() {
        Department department = new Department(0, "Продажа");
        int before = departmentRepository.getAll().size();

        departmentRepository.save(department);

        int after = departmentRepository.getAll().size();
        assertEquals(before + 1, after);
    }

    @Test
    public void save_update() {
        int id = 1;
        Department updatedDepartment = new Department(id, "Продажа");

        departmentRepository.save(updatedDepartment);

        Department actual = departmentRepository.get(id);
        assertEquals(updatedDepartment, actual);
    }

}
