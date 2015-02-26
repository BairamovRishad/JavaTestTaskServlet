package com.barnyard.staff.helpers;

import com.barnyard.staff.domain.Employee;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test.xml")
public class EmployeeJsonDeserializerTests {

    private EmployeeJsonDeserializer employeeJsonDeserializer;

    @Before
    public void setUp() {
        employeeJsonDeserializer = new EmployeeJsonDeserializer();
    }

    @Test
    public void deserialize_ValidEmployee_ShouldReturnDeserializedEmployee() throws Exception {
        Date birthdate = DateFormat.getDateInstance().parse("01.01.2010");
        Employee employee = new Employee.EmployeeBuilder().id(1L).firstName("John").lastName("Doe").salary(50.0)
                .birthdate(birthdate).active(true).build();

        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat formatter = new SimpleDateFormat(Employee.BIRTHDATE_FORMAT);
        mapper.setDateFormat(formatter);
        byte[] jsonBytes = mapper.writeValueAsBytes(employee);
        JsonParser jsonParser = mapper.getFactory().createParser(jsonBytes);

        Employee employeeDeserialized = employeeJsonDeserializer.deserialize(jsonParser, null);

        assertEquals(employee, employeeDeserialized);
    }

}