package com.barnyard.staff.helpers;

import com.barnyard.staff.service.DepartmentService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.reset;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test.xml")
public class NameFormatterTests {

    NameFormatter nameFormatter;

    @Autowired
    private DepartmentService departmentServiceMock;

    @Before
    public void setUp() {
        reset(departmentServiceMock);

        nameFormatter = new NameFormatter();
    }

    @Test
    public void format_NotNullString_ShouldReturnStringInStartCase() {
        String[] strings = {"test", "Test", "TEST", "tEST", " test", "test ", " test "};
        String expectedString = "Test";

        for (String str : strings) {
            String actualString = NameFormatter.format(str);
            assertEquals(expectedString, actualString);
        }
    }

}
