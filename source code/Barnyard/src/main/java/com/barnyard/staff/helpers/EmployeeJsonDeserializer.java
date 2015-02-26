package com.barnyard.staff.helpers;

import com.barnyard.staff.domain.Employee;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EmployeeJsonDeserializer extends JsonDeserializer<Employee> {

    @Override
    public Employee deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        double salary = 0;
        try {
            String salaryText = node.get("salary").asText().replace(',', '.');
            salary = new BigDecimal(salaryText).doubleValue();
        } catch (Exception e) {
        }

        Date birthdate = null;
        try {
            String birthdateText = node.get("birthdate").asText();
            birthdate = SimpleDateFormat.getDateInstance().parse(birthdateText);
        } catch (Exception e) {
        }

        return new Employee.EmployeeBuilder().id(node.get("id").asLong()).firstName(node.get("firstName").asText())
                .lastName(node.get("lastName").asText()).salary(salary).birthdate(birthdate)
                .active(node.get("active").asBoolean()).build();
    }
}