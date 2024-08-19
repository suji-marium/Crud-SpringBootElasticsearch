package com.example.elasticsearchSpring.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseGet {
    private String message;
    private List<EmployeeResponse> details;
}
