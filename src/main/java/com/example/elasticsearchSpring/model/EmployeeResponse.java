package com.example.elasticsearchSpring.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponse {
    private String accountManager;
    private String department; 
    private String id; 
    private List<EmployeeResponseDTO> employeeList;
}
