package com.example.elasticsearchSpring.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.example.elasticsearchSpring.model.EmployeeDetails;

public interface ElasticSearchRepo extends ElasticsearchRepository<EmployeeDetails,String> {
    public List<EmployeeDetails> findAllByManagerId(String managerId);
    List<EmployeeDetails> findAllByDepartment(String department);
    public List<EmployeeDetails> findAll();
}
