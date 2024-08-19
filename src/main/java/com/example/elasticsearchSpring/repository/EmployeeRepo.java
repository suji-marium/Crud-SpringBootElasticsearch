package com.example.elasticsearchSpring.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.example.elasticsearchSpring.model.EmployeeDetails;

@Repository
public interface EmployeeRepo extends ElasticsearchRepository<EmployeeDetails,String>{
    public List<EmployeeDetails> findAllByManagerId(String managerId);
}

