package com.barnyard.staff.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;

public class Department {
    private int id;

    @NotBlank(message = "{invalid.blankField}")
    @Pattern(regexp = "^\\s*(\\s?\\p{L}+)*\\s*$", message = "{invalid.nonalphabetic}")
    private String name;

    public Department() {
    }

    public Department(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (!(obj instanceof Department)) return false;

        Department other = (Department) obj;

        return EqualsBuilder.reflectionEquals(this.id, other.id) &&
                EqualsBuilder.reflectionEquals(this.name, other.name);
    }
}
