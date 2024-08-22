package com.example.elasticsearchSpring.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName="employeelist")
public class EmployeeDetails {
    @Id
    private String id;
    private String name;

    @Pattern(regexp = "Account Manager|associate",message = "Invalid designation")
    private String designation;

    @Pattern(regexp = "sales|delivery|QA|engineering|BA", message = "Invalid department")
    private String department;

    @Email(message = "Invalid email address")
    private String email;
    
    @Pattern(regexp = "\\d{10}", message = "Invalid mobile number")
    private String mobile;
    private String location;
    private String managerId;
    private Date dateOfJoining;
    private Date createdTime;
    private Date updatedTime;
    
}
