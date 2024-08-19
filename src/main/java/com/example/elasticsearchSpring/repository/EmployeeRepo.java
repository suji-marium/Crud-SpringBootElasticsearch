package com.example.elasticsearchSpring.repository;

import java.util.List;
import java.util.Optional;

import com.example.elasticsearchSpring.model.EmployeeDetails;

import jakarta.validation.Valid;

public interface EmployeeRepo{
    public List<EmployeeDetails> findAllByManagerId(String managerId);

    public void save(@Valid EmployeeDetails employeeDetails);

    public List<EmployeeDetails> findAll();

    public Optional<EmployeeDetails> findById(String currentManagerId);

    public void deleteById(String id);

    public boolean existsById(String id);

    public List<EmployeeDetails> findAllByDepartment(String department);
}

