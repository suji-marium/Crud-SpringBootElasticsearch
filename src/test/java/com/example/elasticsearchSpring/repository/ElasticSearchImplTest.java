package com.example.elasticsearchSpring.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.elasticsearchSpring.model.EmployeeDetails;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ElasticSearchImplTest {

    @Mock
    private ElasticSearchRepo elasticSearchRepo;

    @InjectMocks
    private ElasticSearchImpl elasticSearchImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllByManagerId() {
        EmployeeDetails employee = new EmployeeDetails();
        employee.setId("1");
        when(elasticSearchRepo.findAllByManagerId("1")).thenReturn(Collections.singletonList(employee));

        List<EmployeeDetails> employees = elasticSearchImpl.findAllByManagerId("1");
        assertNotNull(employees);
        assertEquals(1, employees.size());
        assertEquals("1", employees.get(0).getId());
    }

    @Test
    void testSave() {
        EmployeeDetails employee = new EmployeeDetails();
        employee.setId("2");

        // No need to specify any behavior for save in this case
        elasticSearchImpl.save(employee);

        verify(elasticSearchRepo, times(1)).save(employee);
    }

    @Test
    void testFindAll() {
        EmployeeDetails employee = new EmployeeDetails();
        employee.setId("3");
        when(elasticSearchRepo.findAll()).thenReturn(Collections.singletonList(employee));

        List<EmployeeDetails> employees = elasticSearchImpl.findAll();
        assertNotNull(employees);
        assertEquals(1, employees.size());
        assertEquals("3", employees.get(0).getId());
    }

    @Test
    void testFindById() {
        EmployeeDetails employee = new EmployeeDetails();
        employee.setId("4");
        when(elasticSearchRepo.findById("4")).thenReturn(Optional.of(employee));

        Optional<EmployeeDetails> foundEmployee = elasticSearchImpl.findById("4");
        assertTrue(foundEmployee.isPresent());
        assertEquals("4", foundEmployee.get().getId());
    }

    @Test
    void testDeleteById() {
        doNothing().when(elasticSearchRepo).deleteById("5");

        elasticSearchImpl.deleteById("5");

        verify(elasticSearchRepo, times(1)).deleteById("5");
    }

    @Test
    void testExistsById() {
        when(elasticSearchRepo.existsById("6")).thenReturn(true);

        boolean exists = elasticSearchImpl.existsById("6");
        assertTrue(exists);
    }

    @Test
    void testFindAllByDepartment() {
        EmployeeDetails employee = new EmployeeDetails();
        employee.setDepartment("IT");
        when(elasticSearchRepo.findAllByDepartment("IT")).thenReturn(Collections.singletonList(employee));

        List<EmployeeDetails> employees = elasticSearchImpl.findAllByDepartment("IT");
        assertNotNull(employees);
        assertEquals(1, employees.size());
        assertEquals("IT", employees.get(0).getDepartment());
    }
}
