package com.barnyard.staff.domain;

import com.barnyard.staff.helpers.EmployeeJsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.util.Date;

@JsonDeserialize(using = EmployeeJsonDeserializer.class)
public class Employee {
    public static final String BIRTHDATE_FORMAT = "dd.MM.yyyy";

    private long id;
    private int departmentId;

    @NotBlank(message = "{invalid.blankField}")
    @Pattern(regexp = "^\\s*\\p{L}+\\s*$", message = "{invalid.nonalphabetic}")
    private String firstName;

    @NotBlank(message = "{invalid.blankField}")
    @Pattern(regexp = "^\\s*\\p{L}+\\s*$", message = "{invalid.nonalphabetic}")
    private String lastName;

    @DecimalMin(value = "0.001", message = "{invalid.nonpositive}")
    @Digits(integer = 13, fraction = 2, message = "{invalid.invalidFormat}")
    private double salary;

    @NotNull(message = "{invalid.incorrectBirthdate}")
    @Past(message = "{invalid.outOfRangeBirthdate}")
    @DateTimeFormat(pattern = BIRTHDATE_FORMAT)
    private Date birthdate;

    private boolean active;

    public Employee() {
    }

    private Employee(EmployeeBuilder builder) {
        this.id = builder.id;
        this.departmentId = builder.departmentId;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.salary = builder.salary;
        this.birthdate = builder.birthdate;
        this.active = builder.active;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (!(obj instanceof Employee)) return false;

        Employee other = (Employee) obj;

        return EqualsBuilder.reflectionEquals(this.id, other.id) &&
                EqualsBuilder.reflectionEquals(this.departmentId, other.departmentId) &&
                EqualsBuilder.reflectionEquals(this.firstName, other.firstName) &&
                EqualsBuilder.reflectionEquals(this.lastName, other.lastName) &&
                EqualsBuilder.reflectionEquals(this.salary, other.salary) &&
                EqualsBuilder.reflectionEquals(this.birthdate, other.birthdate) &&
                EqualsBuilder.reflectionEquals(this.active, other.active);
    }

    public static class EmployeeBuilder {
        private long id;
        private int departmentId;
        private String firstName;
        private String lastName;
        private double salary;
        private Date birthdate;
        private boolean active;

        public EmployeeBuilder id(long id) {
            this.id = id;
            return this;
        }

        public EmployeeBuilder departmentId(int departmentId) {
            this.departmentId = departmentId;
            return this;
        }

        public EmployeeBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public EmployeeBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public EmployeeBuilder salary(double salary) {
            this.salary = salary;
            return this;
        }

        public EmployeeBuilder birthdate(Date birthdate) {
            this.birthdate = birthdate;
            return this;
        }

        public EmployeeBuilder active(boolean active) {
            this.active = active;
            return this;
        }

        public Employee build() {
            return new Employee(this);
        }
    }

}
