package com.example.elasticsearchSpring.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.elasticsearchSpring.model.EmployeeDetails;
import com.example.elasticsearchSpring.model.EmployeeResponseGet;
import com.example.elasticsearchSpring.model.EmployeeResponseUpdate;
import com.example.elasticsearchSpring.repository.EmployeeRepo;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class EmployeeServiceTest {

    @Mock
    private EmployeeRepo employeeRepo;  // Mocking EmployeeRepo

    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddEmployee_Success() {
        EmployeeDetails employeeDetails = new EmployeeDetails();
        employeeDetails.setId("123");
        employeeDetails.setDesignation("Account Manager");
        employeeDetails.setDepartment("Sales");

        // Mock repository behavior
        when(employeeRepo.findAll()).thenReturn(Collections.emptyList());
        when(employeeRepo.findAllByDepartment("Sales")).thenReturn(Collections.emptyList());

        ResponseEntity<EmployeeResponseUpdate> response = employeeService.addEmployee(employeeDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Employee added successfully", response.getBody().getMessage());
    }

    @Test
    void testAddEmployee_Fail_ManagerExists() {
        EmployeeDetails employeeDetails = new EmployeeDetails();
        employeeDetails.setId("123");
        employeeDetails.setDesignation("Account Manager");
        employeeDetails.setDepartment("Sales");

        EmployeeDetails existingManager = new EmployeeDetails();
        existingManager.setId("456");
        existingManager.setDesignation("Account Manager");
        existingManager.setDepartment("Sales");

        when(employeeRepo.findAll()).thenReturn(Collections.singletonList(existingManager));

        ResponseEntity<EmployeeResponseUpdate> response = employeeService.addEmployee(employeeDetails);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Department Sales already has a manager.", response.getBody().getMessage());
    }

    @Test
    void testAddEmployee_Fail_NoManagerForAssociate() {
        EmployeeDetails employeeDetails = new EmployeeDetails();
        employeeDetails.setId("123");
        employeeDetails.setDesignation("associate");
        employeeDetails.setDepartment("Sales");

        when(employeeRepo.findAll()).thenReturn(Collections.emptyList());
        when(employeeRepo.findAllByDepartment("Sales")).thenReturn(Collections.emptyList());

        ResponseEntity<EmployeeResponseUpdate> response = employeeService.addEmployee(employeeDetails);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Department Sales doesn't contain a manager.", response.getBody().getMessage());
    }

    @Test
    void testViewEmployees_Success() {
        EmployeeDetails manager = new EmployeeDetails();
        manager.setId("1");
        manager.setName("John");
        manager.setDesignation("Account Manager");
        manager.setDepartment("Sales");

        EmployeeDetails employee = new EmployeeDetails();
        employee.setId("2");
        employee.setManagerId("1");
        employee.setName("Jane");
        employee.setDesignation("associate");
        employee.setDepartment("Sales");
        employee.setDateOfJoining(LocalDateTime.now().minusYears(2).toString());

        List<EmployeeDetails> employees = Arrays.asList(manager, employee);

        when(employeeRepo.findAll()).thenReturn(employees);
        when(employeeRepo.findById("1")).thenReturn(Optional.of(manager));
        when(employeeRepo.findAllByDepartment("Sales")).thenReturn(Arrays.asList(employee));

        ResponseEntity<EmployeeResponseGet> response = employeeService.viewEmployees(null, "1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully fetched", response.getBody().getMessage());
        //assertFalse(response.getBody().getEmployees().isEmpty());
    }

    @Test
    void testDeleteEmployee_Success() {
        EmployeeDetails employeeDetails = new EmployeeDetails();
        employeeDetails.setId("123");
        employeeDetails.setDesignation("associate");
        employeeDetails.setDepartment("Sales");

        when(employeeRepo.existsById("123")).thenReturn(true);
        when(employeeRepo.findById("123")).thenReturn(Optional.of(employeeDetails));
        when(employeeRepo.findAllByManagerId("123")).thenReturn(Collections.emptyList());

        ResponseEntity<EmployeeResponseUpdate> response = employeeService.deleteEmployee("123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully deleted " + employeeDetails.getName() + " from the employee list of the organization", response.getBody().getMessage());
    }

    @Test
    void testDeleteEmployee_Fail_ManagerHasEmployees() {
        EmployeeDetails employeeDetails = new EmployeeDetails();
        employeeDetails.setId("123");
        employeeDetails.setDesignation("Account Manager");
        employeeDetails.setDepartment("Sales");

        when(employeeRepo.existsById("123")).thenReturn(true);
        when(employeeRepo.findById("123")).thenReturn(Optional.of(employeeDetails));
        when(employeeRepo.findAllByManagerId("123")).thenReturn(Collections.singletonList(new EmployeeDetails()));

        ResponseEntity<EmployeeResponseUpdate> response = employeeService.deleteEmployee("123");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Cannot delete " + employeeDetails.getName() + " because he manages other employees", response.getBody().getMessage());
    }

    @Test
    void testUpdateEmployee_Success() {
        EmployeeDetails oldManager = new EmployeeDetails();
        oldManager.setId("1");
        oldManager.setName("John");

        EmployeeDetails newManager = new EmployeeDetails();
        newManager.setId("2");
        newManager.setName("Jane");

        EmployeeDetails employeeDetails = new EmployeeDetails();
        employeeDetails.setId("123");
        employeeDetails.setManagerId("1");

        when(employeeRepo.findAll()).thenReturn(Collections.singletonList(employeeDetails));
        when(employeeRepo.findById("123")).thenReturn(Optional.of(employeeDetails));
        when(employeeRepo.findById("1")).thenReturn(Optional.of(oldManager));
        when(employeeRepo.findById("2")).thenReturn(Optional.of(newManager));

        ResponseEntity<EmployeeResponseUpdate> response = employeeService.updateEmployee("123", "2");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(employeeDetails.getName() + "'s manager has successfully changed from " + oldManager.getName() + " to " + newManager.getName(), response.getBody().getMessage());
    }

    @Test
    void testUpdateEmployee_Fail_EmployeeNotFound() {
        when(employeeRepo.findById("123")).thenReturn(Optional.empty());

        ResponseEntity<EmployeeResponseUpdate> response = employeeService.updateEmployee("123", "2");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Employee not found", response.getBody().getMessage());
    }

    @Test
    void testUpdateEmployee_Fail_ManagerNotFound() {
        EmployeeDetails employeeDetails = new EmployeeDetails();
        employeeDetails.setId("123");
        employeeDetails.setManagerId("1");

        when(employeeRepo.findById("123")).thenReturn(Optional.of(employeeDetails));
        when(employeeRepo.findById("1")).thenReturn(Optional.empty());

        ResponseEntity<EmployeeResponseUpdate> response = employeeService.updateEmployee("123", "2");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Manager not found", response.getBody().getMessage());
    }

    @Test
    void testUpdateEmployee_Fail_ManagerAsEmployee() {
        EmployeeDetails employeeDetails = new EmployeeDetails();
        employeeDetails.setId("123");
        employeeDetails.setManagerId("123");

        when(employeeRepo.findAll()).thenReturn(Collections.singletonList(employeeDetails));

        ResponseEntity<EmployeeResponseUpdate> response = employeeService.updateEmployee("123", "123");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Manager id cannot be provided as employee id", response.getBody().getMessage());
    }
}
