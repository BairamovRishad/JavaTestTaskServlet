package com.barnyard.staff.mvc;

import com.barnyard.staff.domain.Employee;
import com.barnyard.staff.helpers.EmployeeExtraValidator;
import com.barnyard.staff.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Collection;

@Controller
@Secured("ROLE_READER")
@RequestMapping(value = "/employees")
public class EmployeeController {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeService employeeService;
    private final EmployeeExtraValidator employeeExtraValidator;

    @Autowired
    public EmployeeController(EmployeeService employeeService, EmployeeExtraValidator employeeExtraValidator) {
        this.employeeService = employeeService;
        this.employeeExtraValidator = employeeExtraValidator;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String employees(Model model) {
        Collection<Employee> empList = employeeService.getAllEmployees();
        model.addAttribute("employeeList", empList);
        return "employees/list";
    }

    @RequestMapping(value = "/details/{id}", method = RequestMethod.GET)
    public String details(@PathVariable("id") long id, Model model) {
        Employee employee = employeeService.getEmployee(id);
        model.addAttribute("employee", employee);
        return "employees/details";
    }

    @Secured("ROLE_EDITOR")
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(Model model) {
        Employee employee = new Employee.EmployeeBuilder().active(true).build();
        model.addAttribute("employee", employee);
        return "employees/save";
    }

    @Secured("ROLE_EDITOR")
    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable("id") long id, Model model) throws NotFoundException {
        Employee employee = employeeService.getEmployee(id);
        model.addAttribute("employee", employee);
        return "employees/save";
    }

    @Secured("ROLE_EDITOR")
    @RequestMapping(value = "/save", method = {RequestMethod.PUT, RequestMethod.POST}, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String save(@Valid @RequestBody Employee employee, BindingResult bindingResult, Model model, HttpServletResponse response) {
        employeeExtraValidator.validate(employee, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("employee", employee);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "employees/save";
        }

        employeeService.saveEmployee(employee);
        return null;
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String search(@RequestParam("term") String term, Model model) {
        Collection<Employee> employeeList = employeeService.findEmployees(term);

        if (employeeList.isEmpty()) {
            throw new EmptyResultDataAccessException(1);
        }

        model.addAttribute("employeeList", employeeList);

        return "employees/table";
    }
}