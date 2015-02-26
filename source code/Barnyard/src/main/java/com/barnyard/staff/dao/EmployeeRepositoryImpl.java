package com.barnyard.staff.dao;

import com.barnyard.staff.domain.Employee;
import com.barnyard.staff.helpers.NameFormatter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

@Repository
public class EmployeeRepositoryImpl implements EmployeeRepository {

    private JdbcTemplate jdbc;

    @Inject
    public EmployeeRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbc = jdbcTemplate;
    }


    private String formatToISO8601(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }

    @Override
    public Employee get(long id) {
        return jdbc.queryForObject(
                "select id, department_id, first_name, last_name, salary, birthdate, active from employee where id = ?",
                new EmployeeRowMapper(), id);
    }

    @Override
    public Collection<Employee> getAll() {
        return jdbc.query("select id, department_id, first_name, last_name, salary, birthdate, active from employee",
                new EmployeeRowMapper());
    }

    @Override
    public Collection<Employee> findEmployees(String term) {
        String queryStr = term.replaceAll("\\*", "%").replaceAll("\\?", "_");

        return jdbc.query(
                "select id, department_id, first_name, last_name, salary, birthdate, active from employee as e where lower(concat(e.last_name, e.first_name)) like ?",
                new EmployeeRowMapper(), queryStr);
    }

    @Override
    public void save(Employee employee) {
        String formattedFirstName = NameFormatter.format(employee.getFirstName());
        String formattedLastName = NameFormatter.format(employee.getLastName());
        String formattedDate = formatToISO8601(employee.getBirthdate());

        if (employee.getId() == 0) {
            jdbc.update("insert into employee (first_name, last_name, birthdate, salary, active) values (?,?,?,?,?)",
                    formattedFirstName, formattedLastName, formattedDate, employee.getSalary(), employee.isActive());
        } else {
            jdbc.update("update employee set first_name=?, last_name=?, birthdate=?, salary=? where id=?",
                    formattedFirstName, formattedLastName, formattedDate, employee.getSalary(), employee.getId());
        }
    }

    private static class EmployeeRowMapper implements RowMapper<Employee> {
        public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
            Employee employee = new Employee.EmployeeBuilder().id(rs.getLong("id"))
                    .departmentId(rs.getInt("department_id")).firstName(rs.getString("first_name"))
                    .lastName(rs.getString("last_name")).salary(rs.getDouble("salary"))
                    .birthdate(rs.getDate("birthdate")).active(rs.getBoolean("active")).build();

            return employee;
        }
    }

}