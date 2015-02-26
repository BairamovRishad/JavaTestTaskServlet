package com.barnyard.staff.mvc;

import com.barnyard.staff.domain.Employee;
import com.barnyard.staff.helpers.EmployeeExtraValidator;
import com.barnyard.staff.service.EmployeeService;
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
import org.springframework.web.util.NestedServletException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.barnyard.staff.TestUtil.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test.xml", "file:src/main/webapp/WEB-INF/config/root.xml"})
@WebAppConfiguration
public class EmployeeControllerTests {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private EmployeeService employeeServiceMock;

    @Autowired
    private EmployeeExtraValidator employeeExtraValidatorMock;

    @Before
    public void setUp() {
        reset(employeeServiceMock);
        reset(employeeExtraValidatorMock);

        SecurityContextHolder.clearContext();

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void employees_EmployeesFound_ShouldAddEntriesToModelAndRenderEmployeeListView() throws Exception {
        setupUser("ROLE_READER");

        Employee first = new Employee.EmployeeBuilder().id(0L).firstName("John").lastName("Doe").build();
        Employee second = new Employee.EmployeeBuilder().id(1L).firstName("Jack").lastName("Doe").build();
        List<Employee> expectedEmployees = Arrays.asList(first, second);

        when(employeeServiceMock.getAllEmployees()).thenReturn(expectedEmployees);

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/list"))
                .andExpect(model().attribute("employeeList", hasSize(2)))
                .andExpect(model().attribute("employeeList", hasItems(expectedEmployees.toArray())));

        verify(employeeServiceMock, times(1)).getAllEmployees();
        verifyNoMoreInteractions(employeeServiceMock);
    }

    @Test
    public void details_EmployeeNotFound_ShouldRenderToErrorModalView() throws Exception {
        setupUser("ROLE_READER");

        String errorMsg = "Employee not found!";
        when(employeeServiceMock.getEmployee(1L)).thenThrow(new NotFoundException(errorMsg));

        mockMvc.perform(get("/employees/details/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(model().attribute("errorMessage", errorMsg))
                .andExpect(view().name("error/error_modal"));

        verify(employeeServiceMock, times(1)).getEmployee(1L);
        verifyNoMoreInteractions(employeeServiceMock);
    }

    @Test
    public void details_EmployeeFound_ShouldAddEntryToModelAndRenderEmployeeDetailsView() throws Exception {
        setupUser("ROLE_READER");

        Employee employee = new Employee.EmployeeBuilder().id(1L).firstName("John").lastName("Doe").build();

        when(employeeServiceMock.getEmployee(employee.getId())).thenReturn(employee);

        mockMvc.perform(get("/employees/details/{id}", employee.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/details"))
                .andExpect(model().attribute("employee", hasProperty("id", is(employee.getId()))))
                .andExpect(model().attribute("employee", hasProperty("firstName", is(employee.getFirstName()))))
                .andExpect(model().attribute("employee", hasProperty("lastName", is(employee.getLastName()))));

        verify(employeeServiceMock, times(1)).getEmployee(employee.getId());
        verifyNoMoreInteractions(employeeServiceMock);
    }

    @Test
    public void create_NewEmployee_ShouldAddActiveEntryToModelAndRenderEmployeeCreateView() throws Exception {
        setupUser("ROLE_EDITOR");

        mockMvc.perform(get("/employees/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/save"))
                .andExpect(model().attribute("employee", hasProperty("active", is(true))));
    }

    @Test
    public void edit_EmployeeNotFound_ShouldRenderToErrorModalView() throws Exception {
        setupUser("ROLE_EDITOR");

        String errorMsg = "Employee not found!";
        when(employeeServiceMock.getEmployee(1L)).thenThrow(new NotFoundException(errorMsg));

        mockMvc.perform(get("/employees/edit/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(model().attribute("errorMessage", errorMsg))
                .andExpect(view().name("error/error_modal"));

        verify(employeeServiceMock, times(1)).getEmployee(1L);
        verifyNoMoreInteractions(employeeServiceMock);
    }

    @Test
    public void edit_EmployeeFound_ShouldAddEntryToModelAndRenderEmployeeEditView() throws Exception {
        setupUser("ROLE_EDITOR");

        Employee employee = new Employee.EmployeeBuilder().id(1L).firstName("John").lastName("Doe").build();

        when(employeeServiceMock.getEmployee(employee.getId())).thenReturn(employee);

        mockMvc.perform(get("/employees/edit/{id}", employee.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/save"))
                .andExpect(model().attribute("employee", hasProperty("id", is(employee.getId()))))
                .andExpect(model().attribute("employee", hasProperty("firstName", is(employee.getFirstName()))))
                .andExpect(model().attribute("employee", hasProperty("lastName", is(employee.getLastName()))));

        verify(employeeServiceMock, times(1)).getEmployee(employee.getId());
        verifyNoMoreInteractions(employeeServiceMock);
    }

    @Test
    public void save_ValidEmployee_ShouldSaveEntryInDB() throws Exception {
        setupUser("ROLE_EDITOR");

        Date birthdate = DateFormat.getDateInstance().parse("01.01.2010");
        Employee employee = new Employee.EmployeeBuilder().id(1L).firstName("John").lastName("Doe").salary(50.50)
                .birthdate(birthdate).active(true).build();

        mockMvc.perform(post("/employees/save").contentType(APPLICATION_JSON_UTF8).content(convertObjectToJsonBytes(employee)))
                .andExpect(status().isOk());

        ArgumentCaptor<Employee> empCaptor = ArgumentCaptor.forClass(Employee.class);
        ArgumentCaptor<BindingResult> brCaptor = ArgumentCaptor.forClass(BindingResult.class);
        verify(employeeExtraValidatorMock, times(1)).validate(empCaptor.capture(), brCaptor.capture());
        verify(employeeServiceMock, times(1)).saveEmployee(empCaptor.capture());
        verifyNoMoreInteractions(employeeServiceMock);
    }

    @Test
    public void save_EmptyEmployee_ShouldFailValidationAndRenderViewWithErrorMessages() throws Exception {
        setupUser("ROLE_EDITOR");

        Employee employee = new Employee.EmployeeBuilder().firstName("").lastName("").build();

        mockMvc.perform(post("/employees/save").contentType(APPLICATION_JSON_UTF8).content(convertObjectToJsonBytes(employee)))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("employees/save"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeErrorCount("employee", 6))
                .andExpect(model().attribute("employee", hasProperty("firstName", is(employee.getFirstName()))))
                .andExpect(model().attributeHasFieldErrorCode("employee", "firstName",
                        anyOf(is("Pattern"), is("NotBlank"))))
                .andExpect(model().attribute("employee", hasProperty("lastName", is(employee.getLastName()))))
                .andExpect(model().attributeHasFieldErrorCode("employee", "lastName",
                        anyOf(is("Pattern"), is("NotBlank"))))
                .andExpect(model().attribute("employee", hasProperty("salary", is(employee.getSalary()))))
                .andExpect(model().attributeHasFieldErrorCode("employee", "salary", "DecimalMin"))
                .andExpect(model().attribute("employee", hasProperty("birthdate", is(employee.getBirthdate()))))
                .andExpect(model().attributeHasFieldErrorCode("employee", "birthdate", "NotNull"));

        ArgumentCaptor<Employee> empCaptor = ArgumentCaptor.forClass(Employee.class);
        ArgumentCaptor<BindingResult> brCaptor = ArgumentCaptor.forClass(BindingResult.class);
        verify(employeeExtraValidatorMock, times(1)).validate(empCaptor.capture(), brCaptor.capture());
        verify(employeeServiceMock, times(0)).saveEmployee(empCaptor.capture());
        verifyNoMoreInteractions(employeeServiceMock);
    }

    @Test
    public void save_EmployeesWithValidFirstNames_ShouldPassFirstNameValidation() throws Exception {
        setupUser("ROLE_EDITOR");

        String[] validFirstNames = {"John", "  John", "John  ", "  John  "};
        Date birthdate = DateFormat.getDateInstance().parse("01.01.2010");

        for (String firstName : validFirstNames) {
            Employee employee = new Employee.EmployeeBuilder().id(1L).firstName(firstName).lastName("Doe").salary(50.50)
                    .birthdate(birthdate).active(true).build();

            mockMvc.perform(post("/employees/save").contentType(APPLICATION_JSON_UTF8).content(convertObjectToJsonBytes(employee)))
                    .andExpect(status().isOk());
        }

        ArgumentCaptor<Employee> empCaptor = ArgumentCaptor.forClass(Employee.class);
        ArgumentCaptor<BindingResult> brCaptor = ArgumentCaptor.forClass(BindingResult.class);
        verify(employeeExtraValidatorMock, times(validFirstNames.length)).validate(empCaptor.capture(), brCaptor.capture());
        verify(employeeServiceMock, times(validFirstNames.length)).saveEmployee(empCaptor.capture());
        verifyNoMoreInteractions(employeeServiceMock);
    }

    @Test
    public void save_EmployeesWithInvalidFirstNames_ShouldFailValidationAndRenderViewWithErrorMessages() throws Exception {
        setupUser("ROLE_EDITOR");

        String[] invalidFirstNames = {"", "  ", "John Jack", "John4", "/_"};
        Date birthdate = DateFormat.getDateInstance().parse("01.01.2010");

        for (String firstName : invalidFirstNames) {
            Employee employee = new Employee.EmployeeBuilder().id(1L).firstName(firstName).lastName("Doe").salary(50.50)
                    .birthdate(birthdate).active(true).build();

            mockMvc.perform(post("/employees/save").contentType(APPLICATION_JSON_UTF8).content(convertObjectToJsonBytes(employee)))
                    .andExpect(status().isBadRequest())
                    .andExpect(view().name("employees/save"))
                    .andExpect(model().attribute("employee", hasProperty("firstName", is(employee.getFirstName()))))
                    .andExpect(model().attributeHasFieldErrorCode("employee", "firstName",
                            anyOf(is("Pattern"), is("NotBlank"))));
        }

        ArgumentCaptor<Employee> empCaptor = ArgumentCaptor.forClass(Employee.class);
        ArgumentCaptor<BindingResult> brCaptor = ArgumentCaptor.forClass(BindingResult.class);
        verify(employeeExtraValidatorMock, times(invalidFirstNames.length)).validate(empCaptor.capture(), brCaptor.capture());
        verify(employeeServiceMock, times(0)).saveEmployee(empCaptor.capture());
        verifyNoMoreInteractions(employeeServiceMock);
    }

    @Test
    public void save_EmployeesWithValidLastNames__ShouldPassLastNameValidation() throws Exception {
        setupUser("ROLE_EDITOR");

        String[] validLastNames = {"Doe", "  Doe", "Doe  ", "  Doe  "};
        Date birthdate = DateFormat.getDateInstance().parse("01.01.2010");

        for (String lastName : validLastNames) {
            Employee employee = new Employee.EmployeeBuilder().id(1L).firstName("John").lastName(lastName).salary(50.50)
                    .birthdate(birthdate).active(true).build();

            mockMvc.perform(post("/employees/save").contentType(APPLICATION_JSON_UTF8).content(convertObjectToJsonBytes(employee)))
                    .andExpect(status().isOk());
        }

        ArgumentCaptor<Employee> empCaptor = ArgumentCaptor.forClass(Employee.class);
        ArgumentCaptor<BindingResult> brCaptor = ArgumentCaptor.forClass(BindingResult.class);
        verify(employeeExtraValidatorMock, times(validLastNames.length)).validate(empCaptor.capture(), brCaptor.capture());
        verify(employeeServiceMock, times(validLastNames.length)).saveEmployee(empCaptor.capture());
        verifyNoMoreInteractions(employeeServiceMock);
    }

    @Test
    public void save_EmployeesWithInvalidLastNames_ShouldFailValidationAndRenderViewWithErrorMessages() throws Exception {
        setupUser("ROLE_EDITOR");

        String[] invalidLastNames = {"", "  ", "Doe Doe", "Doe4", "/_"};
        Date birthdate = DateFormat.getDateInstance().parse("01.01.2010");

        for (String lastName : invalidLastNames) {
            Employee employee = new Employee.EmployeeBuilder().id(1L).firstName("John").lastName(lastName).salary(50.50)
                    .birthdate(birthdate).active(true).build();

            mockMvc.perform(post("/employees/save").contentType(APPLICATION_JSON_UTF8).content(convertObjectToJsonBytes(employee)))
                    .andExpect(status().isBadRequest())
                    .andExpect(view().name("employees/save"))
                    .andExpect(model().attribute("employee", hasProperty("lastName", is(employee.getLastName()))))
                    .andExpect(model().attributeHasFieldErrorCode("employee", "lastName",
                            anyOf(is("Pattern"), is("NotBlank"))));
        }

        ArgumentCaptor<Employee> empCaptor = ArgumentCaptor.forClass(Employee.class);
        ArgumentCaptor<BindingResult> brCaptor = ArgumentCaptor.forClass(BindingResult.class);
        verify(employeeExtraValidatorMock, times(invalidLastNames.length)).validate(empCaptor.capture(), brCaptor.capture());
        verify(employeeServiceMock, times(0)).saveEmployee(empCaptor.capture());
        verifyNoMoreInteractions(employeeServiceMock);
    }

    @Test
    public void save_EmployeesWithInvalidSalary_ShouldFailValidationAndRenderViewWithErrorMessages() throws Exception {
        setupUser("ROLE_EDITOR");

        Double[] invalidSalaries = {-12345678901234.0, -1.123, -12345678901234.123, 0.0, 12345678901234.0, 1.123, 12345678901234.123};
        Date birthdate = DateFormat.getDateInstance().parse("01.01.2010");

        for (Double salary : invalidSalaries) {
            Employee employee = new Employee.EmployeeBuilder().id(1L).firstName("John").lastName("Doe").salary(salary)
                    .birthdate(birthdate).active(true).build();

            mockMvc.perform(post("/employees/save").contentType(APPLICATION_JSON_UTF8).content(convertObjectToJsonBytes(employee)))
                    .andExpect(status().isBadRequest())
                    .andExpect(view().name("employees/save"))
                    .andExpect(model().attribute("employee", hasProperty("salary", is(employee.getSalary()))))
                    .andExpect(model().attributeHasFieldErrorCode("employee", "salary",
                            anyOf(is("Digits"), is("DecimalMin"))));
        }

        ArgumentCaptor<Employee> empCaptor = ArgumentCaptor.forClass(Employee.class);
        ArgumentCaptor<BindingResult> brCaptor = ArgumentCaptor.forClass(BindingResult.class);
        verify(employeeExtraValidatorMock, times(invalidSalaries.length)).validate(empCaptor.capture(), brCaptor.capture());
        verify(employeeServiceMock, times(0)).saveEmployee(empCaptor.capture());
        verifyNoMoreInteractions(employeeServiceMock);
    }

    @Test
    public void save_EmployeesWithInvalidBirthdates_ShouldFailValidationAndRenderViewWithErrorMessages() throws Exception {
        setupUser("ROLE_EDITOR");

        SimpleDateFormat dateFormat = new SimpleDateFormat(Employee.BIRTHDATE_FORMAT);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        Date future = dateFormat.parse(dateFormat.format(calendar.getTime()));

        Date[] invalidBirthdates = {null, future};

        for (Date birthdate : invalidBirthdates) {
            Employee employee = new Employee.EmployeeBuilder().id(1L).firstName("John").lastName("Doe").salary(50.0)
                    .birthdate(birthdate).active(true).build();

            mockMvc.perform(post("/employees/save").contentType(APPLICATION_JSON_UTF8).content(convertObjectToJsonBytes(employee)))
                    .andExpect(status().isBadRequest())
                    .andExpect(view().name("employees/save"))
                    .andExpect(model().attribute("employee", hasProperty("birthdate", is(employee.getBirthdate()))))
                    .andExpect(model().attributeHasFieldErrorCode("employee", "birthdate",
                            anyOf(is("NotNull"), is("Past"))));
        }

        ArgumentCaptor<Employee> empCaptor = ArgumentCaptor.forClass(Employee.class);
        ArgumentCaptor<BindingResult> brCaptor = ArgumentCaptor.forClass(BindingResult.class);
        verify(employeeExtraValidatorMock, times(invalidBirthdates.length)).validate(empCaptor.capture(), brCaptor.capture());
        verify(employeeServiceMock, times(0)).saveEmployee(empCaptor.capture());
        verifyNoMoreInteractions(employeeServiceMock);
    }

    @Test
    public void search_EmployeesFound_ShouldAddEntriesToModelAndRenderEmployeeTableView() throws Exception {
        setupUser("ROLE_READER");

        String term = "Doe*";
        Employee first = new Employee.EmployeeBuilder().id(0L).firstName("John").lastName("Doe").build();
        Employee second = new Employee.EmployeeBuilder().id(1L).firstName("Jack").lastName("Doe").build();
        List<Employee> expectedEmployees = Arrays.asList(first, second);

        when(employeeServiceMock.findEmployees(term)).thenReturn(Arrays.asList(first, second));

        mockMvc.perform(get("/employees/search").param("term", term))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/table"))
                .andExpect(model().attribute("employeeList", hasSize(2)))
                .andExpect(model().attribute("employeeList", hasItems(expectedEmployees.toArray())));

        verify(employeeServiceMock, times(1)).findEmployees(term);
        verifyNoMoreInteractions(employeeServiceMock);
    }

    @Test(expected = NestedServletException.class)
    public void search_EmployeesNotFound_ShouldThrowException() throws Exception {
        setupUser("ROLE_READER");

        String term = "Doe*";

        when(employeeServiceMock.findEmployees(term)).thenReturn(new ArrayList<Employee>());

        mockMvc.perform(get("/employees/search").param("term", term));
    }

}