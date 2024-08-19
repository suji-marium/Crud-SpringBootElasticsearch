package com.example.elasticsearchSpring.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.elasticsearchSpring.model.EmployeeDetails;

import jakarta.validation.Valid;

@Repository
public class ElasticSearchImpl implements EmployeeRepo {

    @Autowired
    ElasticSearchRepo elasticSearchRepo;
    @Override
    public List<EmployeeDetails> findAllByManagerId(String managerId) {
        return elasticSearchRepo.findAllByManagerId(managerId);
    }

    @Override
    public void save(@Valid EmployeeDetails employeeDetails) {
        elasticSearchRepo.save(employeeDetails);
    }

    @Override
    public List<EmployeeDetails> findAll() {
        return elasticSearchRepo.findAll();
    }

    @Override
    public Optional<EmployeeDetails> findById(String currentManagerId) {
        return elasticSearchRepo.findById(currentManagerId);
    }

    @Override
    public void deleteById(String id) {
        elasticSearchRepo.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return elasticSearchRepo.existsById(id);
    
    }

    @Override
    public List<EmployeeDetails> findAllByDepartment(String department) {
        return elasticSearchRepo.findAllByDepartment(department);
    }
    
}
