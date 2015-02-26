package com.barnyard.staff.dao;

import com.barnyard.staff.domain.Department;
import com.barnyard.staff.helpers.NameFormatter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Repository
public class DepartmentRepositoryImpl implements DepartmentRepository {

    private JdbcTemplate jdbc;

    @Inject
    public DepartmentRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbc = jdbcTemplate;
    }

    @Override
    public Department get(long id) {
        return jdbc.queryForObject("select id, name from department where id = ?", new DepartmentRowMapper(), id);
    }

    @Override
    public Collection<Department> getAll() {
        return jdbc.query("select id, name from department", new DepartmentRowMapper());
    }

    @Override
    public Collection<Department> findDepartments(String name) {
        return jdbc.query("select id, name from department where name = ?", new DepartmentRowMapper(), name);
    }

    @Override
    public void save(Department department) {
        String formattedName = NameFormatter.format(department.getName());

        if (department.getId() == 0) {
            jdbc.update("insert into department (name) values (?)", formattedName);
        } else {
            jdbc.update("update Department set name=? where id=?", formattedName, department.getId());
        }
    }

    private static class DepartmentRowMapper implements RowMapper<Department> {
        public Department mapRow(ResultSet rs, int rowNum) throws SQLException {
            Department department = new Department(rs.getInt("id"), rs.getString("name"));
            return department;
        }
    }

}