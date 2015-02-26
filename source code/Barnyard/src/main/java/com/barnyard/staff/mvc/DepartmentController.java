package com.barnyard.staff.mvc;

import com.barnyard.staff.domain.Department;
import com.barnyard.staff.helpers.DepartmentExtraValidator;
import com.barnyard.staff.service.DepartmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Collection;

@Controller
@Secured("ROLE_EDITOR")
@RequestMapping(value = "/departments")
public class DepartmentController {
    private static final Logger logger = LoggerFactory.getLogger(DepartmentController.class);

    private final DepartmentService departmentService;
    private final DepartmentExtraValidator departmentExtraValidator;

    @Autowired
    public DepartmentController(DepartmentService departmentService, DepartmentExtraValidator departmentExtraValidator) {
        this.departmentService = departmentService;
        this.departmentExtraValidator = departmentExtraValidator;
    }

    private static boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    @RequestMapping(method = RequestMethod.GET)
    public String divisions(Model model, HttpServletRequest request) {
        Collection<Department> departmentList = departmentService.getAllDepartments();
        model.addAttribute("departmentList", departmentList);
        return isAjax(request) ? "departments/table" : "departments/list";
    }

    @RequestMapping(value = "/details/{id}", method = RequestMethod.GET)
    public String details(@PathVariable("id") int id, Model model) {
        Department department = departmentService.getDepartment(id);
        model.addAttribute("department", department);
        return "departments/details";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(Model model) {
        Department department = new Department();
        model.addAttribute("department", department);
        return "departments/save";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable("id") long id, Model model, HttpServletRequest request) {
        Department department = departmentService.getDepartment(id);
        model.addAttribute("department", department);
        return "departments/save";
    }

    @RequestMapping(value = "/save", method = {RequestMethod.PUT, RequestMethod.POST})
    public String save(@Valid @RequestBody Department department, BindingResult bindingResult, Model model, HttpServletResponse response) {
        departmentExtraValidator.validate(department, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("department", department);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "departments/save";
        }

        departmentService.saveDepartment(department);
        return null;
    }
}
