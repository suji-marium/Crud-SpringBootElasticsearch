package com.example.elasticsearchSpring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.elasticsearchSpring.model.*;
import com.example.elasticsearchSpring.repository.EmployeeRepo;
import com.example.elasticsearchSpring.service.EmployeeService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/elasticsearch")
public class EmployeeController {
    
    @Autowired
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService, EmployeeRepo employeeRepo) {
        this.employeeService = employeeService;
    }

    @PostMapping("/addEmployee")
    public ResponseEntity<EmployeeResponseUpdate> addEmployee(@Valid @RequestBody EmployeeDetails employeeDetails){
            return employeeService.addEmployee(employeeDetails);
    }

    @GetMapping("/getEmployee")
    public ResponseEntity<EmployeeResponseGet> viewEmployees(
        @RequestParam(value = "year-of-experience", required = false) Integer yearsOfExperience,
        @RequestParam(value = "managerId", required = false) String managerId){
                return employeeService.viewEmployees(yearsOfExperience,managerId);
    }

    @DeleteMapping("/deleteEmployee")
    public ResponseEntity<EmployeeResponseUpdate> deleteEmployee(@RequestParam (value = "employeeId") String id){
        return employeeService.deleteEmployee(id);
    }

    @PutMapping("/updateEmployee")
    public ResponseEntity<EmployeeResponseUpdate> updateEmployee(
        @RequestParam(value = "employeeId") String id, @RequestParam(value = "managerId") String managerId)
    {
        return employeeService.updateEmployee(id,managerId);
    }
}
