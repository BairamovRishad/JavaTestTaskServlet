package com.barnyard.staff.mvc;

import com.barnyard.staff.domain.Department;
import com.barnyard.staff.helpers.DepartmentExtraValidator;
import com.barnyard.staff.service.DepartmentService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static com.barnyard.staff.TestUtil.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test.xml", "file:src/main/webapp/WEB-INF/config/root.xml"})
@WebAppConfiguration
public class DepartmentControllerTests {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DepartmentService departmentServiceMock;

    @Autowired
    private DepartmentExtraValidator departmentExtraValidatorMock;

    @Before
    public void setUp() {
        reset(departmentServiceMock);
        reset(departmentExtraValidatorMock);

        SecurityContextHolder.clearContext();
        setupUser("ROLE_EDITOR");

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void departments_NormalRequest_ShouldAddEntriesToModelAndRenderDepartmentListView() throws Exception {
        Department first = new Department(0, "Бухгалтерия");
        Department second = new Department(1, "Продажа");
        List<Department> expectedDepartments = Arrays.asList(first, second);

        when(departmentServiceMock.getAllDepartments()).thenReturn(Arrays.asList(first, second));

        mockMvc.perform(get("/departments"))
                .andExpect(status().isOk())
                .andExpect(view().name("departments/list"))
                .andExpect(model().attribute("departmentList", hasSize(2)))
                .andExpect(model().attribute("departmentList", hasItems(expectedDepartments.toArray())));

        verify(departmentServiceMock, times(1)).getAllDepartments();
        verifyNoMoreInteractions(departmentServiceMock);
    }

    @Test
    public void departments_AjaxRequest_ShouldAddEntriesToModelAndRenderDepartmentTableView() throws Exception {
        Department first = new Department(0, "Бухгалтерия");
        Department second = new Department(1, "Продажа");
        List<Department> expectedDepartments = Arrays.asList(first, second);

        when(departmentServiceMock.getAllDepartments()).thenReturn(Arrays.asList(first, second));

        mockMvc.perform(get("/departments").header("X-Requested-With", "XMLHttpRequest"))
                .andExpect(status().isOk())
                .andExpect(view().name("departments/table"))
                .andExpect(model().attribute("departmentList", hasSize(2)))
                .andExpect(model().attribute("departmentList", hasItems(expectedDepartments.toArray())));

        verify(departmentServiceMock, times(1)).getAllDepartments();
        verifyNoMoreInteractions(departmentServiceMock);
    }

    @Test
    public void details_DepartmentNotFound_ShouldRenderToErrorModalView() throws Exception {
        String errorMsg = "Department not found!";
        when(departmentServiceMock.getDepartment(1)).thenThrow(new NotFoundException(errorMsg));

        mockMvc.perform(get("/departments/details/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(model().attribute("errorMessage", errorMsg))
                .andExpect(view().name("error/error_modal"));

        verify(departmentServiceMock, times(1)).getDepartment(1L);
        verifyNoMoreInteractions(departmentServiceMock);
    }

    @Test
    public void details_DepartmentFound_ShouldAddEntryToModelAndRenderDepartmentDetailsView() throws Exception {
        Department department = new Department(0, "Бухгалтерия");

        when(departmentServiceMock.getDepartment(department.getId())).thenReturn(department);

        mockMvc.perform(get("/departments/details/{id}", department.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("departments/details"))
                .andExpect(model().attribute("department", hasProperty("id", is(department.getId()))))
                .andExpect(model().attribute("department", hasProperty("name", is(department.getName()))));

        verify(departmentServiceMock, times(1)).getDepartment(department.getId());
        verifyNoMoreInteractions(departmentServiceMock);
    }

    @Test
    public void create_NewDepartment_ShouldAddActiveEntryToModelAndRenderDepartmentCreateView() throws Exception {
        mockMvc.perform(get("/departments/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("departments/save"));
    }

    @Test
    public void edit_DepartmentNotFound_ShouldRenderToErrorModalView() throws Exception {
        String errorMsg = "Department not found!";
        when(departmentServiceMock.getDepartment(1)).thenThrow(new NotFoundException(errorMsg));

        mockMvc.perform(get("/departments/edit/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(model().attribute("errorMessage", errorMsg))
                .andExpect(view().name("error/error_modal"));

        verify(departmentServiceMock, times(1)).getDepartment(1);
        verifyNoMoreInteractions(departmentServiceMock);
    }

    @Test
    public void edit_DepartmentFound_ShouldAddEntryToModelAndRenderDepartmentEditView() throws Exception {
        Department department = new Department(0, "Бухгалтерия");

        when(departmentServiceMock.getDepartment(department.getId())).thenReturn(department);

        mockMvc.perform(get("/departments/edit/{id}", department.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("departments/save"))
                .andExpect(model().attribute("department", hasProperty("id", is(department.getId()))))
                .andExpect(model().attribute("department", hasProperty("name", is(department.getName()))));

        verify(departmentServiceMock, times(1)).getDepartment(department.getId());
        verifyNoMoreInteractions(departmentServiceMock);
    }

    @Test
    public void save_ValidDepartment_ShouldSaveEntryInDB() throws Exception {
        String[] validNames = {"Бухгалтерия", "  Бухгалтерия", "Бухгалтерия  ", "  Бухгалтерия  ",
                "Ремонт и реконструкция", "  Ремонт и реконструкция", "Ремонт и реконструкция  ", "  Ремонт и реконструкция  "};

        for (String name : validNames) {
            Department department = new Department(0, name);

            mockMvc.perform(post("/departments/save").contentType(APPLICATION_JSON_UTF8).content(convertObjectToJsonBytes(department)))
                    .andExpect(status().isOk());
        }

        ArgumentCaptor<Department> empCaptor = ArgumentCaptor.forClass(Department.class);
        ArgumentCaptor<BindingResult> brCaptor = ArgumentCaptor.forClass(BindingResult.class);
        verify(departmentExtraValidatorMock, times(validNames.length)).validate(empCaptor.capture(), brCaptor.capture());
        verify(departmentServiceMock, times(validNames.length)).saveDepartment(empCaptor.capture());
        verifyNoMoreInteractions(departmentServiceMock);
    }

    @Test
    public void save_DepartmentsWithInvalidNames_ShouldFailValidationAndRenderViewWithErrorMessages() throws Exception {
        String[] invalidNames = {"", "  ", "Бухгалтерия4", "/_", "Ремонт  и реконструкция"};

        for (String name : invalidNames) {
            Department department = new Department(0, name);

            mockMvc.perform(post("/departments/save").contentType(APPLICATION_JSON_UTF8).content(convertObjectToJsonBytes(department)))
                    .andExpect(status().isBadRequest())
                    .andExpect(view().name("departments/save"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attribute("department", hasProperty("name", is(department.getName()))))
                    .andExpect(model().attributeHasFieldErrorCode("department", "name",
                            anyOf(is("NotBlank"), is("Pattern"))));
        }

        ArgumentCaptor<Department> empCaptor = ArgumentCaptor.forClass(Department.class);
        ArgumentCaptor<BindingResult> brCaptor = ArgumentCaptor.forClass(BindingResult.class);
        verify(departmentExtraValidatorMock, times(invalidNames.length)).validate(empCaptor.capture(), brCaptor.capture());
        verify(departmentServiceMock, times(0)).saveDepartment(empCaptor.capture());
        verifyNoMoreInteractions(departmentServiceMock);
    }
}