package com.example.elasticsearchSpring.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import com.example.elasticsearchSpring.model.EmployeeDetails;
import com.example.elasticsearchSpring.model.EmployeeResponse;
import com.example.elasticsearchSpring.model.EmployeeResponseDTO;
import com.example.elasticsearchSpring.model.EmployeeResponseGet;
import com.example.elasticsearchSpring.model.EmployeeResponseUpdate;
import com.example.elasticsearchSpring.model.IdGenerator;
import com.example.elasticsearchSpring.repository.EmployeeRepo;

@Service
public class EmployeeService {
    @Autowired
    EmployeeRepo employeeRepo;
    public ResponseEntity<EmployeeResponseUpdate> addEmployee(EmployeeDetails employeeDetails) {
        
        Date today=new Date();
        employeeDetails.setId(IdGenerator.generateId());
        employeeDetails.setCreatedTime(today);
        employeeDetails.setUpdatedTime(today);
        employeeRepo.save(employeeDetails);
        EmployeeResponseUpdate employeeResponseUpdate=new EmployeeResponseUpdate("Employee added successfully");
        return ResponseEntity.ok(employeeResponseUpdate);
    }
    public ResponseEntity<EmployeeResponseGet> viewEmployees(Integer yearsOfExperience, String managerId) {

        List<EmployeeDetails> employees = StreamSupport.stream(employeeRepo.findAll().spliterator(), false)
        .collect(Collectors.toList());
        // Creating a set with all manager IDs
        Set<String> allManagerIds = new HashSet<>();
        for (EmployeeDetails employeeDetails : employees) {
            if ("Account Manager".equals(employeeDetails.getDesignation())) {
                allManagerIds.add(employeeDetails.getId());
            }
        }

        // Group by managerId
        Map<String, List<EmployeeDetails>> employeesByManager = employees.stream()
            .filter(emp -> emp.getManagerId() != null) // Ensure no null keys
            .collect(Collectors.groupingBy(EmployeeDetails::getManagerId, Collectors.toList()));

        for (String mngId : allManagerIds) {
            employeesByManager.putIfAbsent(mngId, new ArrayList<>());
        }

    
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        // Create the filtered responses
        List<EmployeeResponse> filteredResponses = employeesByManager.entrySet().stream()
            .map(entry -> {
                String currentManagerId = entry.getKey();
                List<EmployeeDetails> employeeList = entry.getValue();

                Optional<EmployeeDetails> managerOpt = employeeRepo.findById(currentManagerId);
                String managerName = managerOpt.map(EmployeeDetails::getName).orElse("Unknown");
                String managerDept = managerOpt.map(EmployeeDetails::getDepartment).orElse("Unknown");

                // Filter employees based on condition
                List<EmployeeResponseDTO> filteredEmployeeList = employeeList.stream()
                    .filter(employee -> {
                        String dateOfJoining = employee.getDateOfJoining();
                        if (dateOfJoining == null) return false; // Skip if dateOfJoining is null
                        LocalDateTime joiningDate = LocalDateTime.parse(employee.getDateOfJoining(), formatter);
                        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
                        int yearsOfExperienceCalculated = (int) ChronoUnit.YEARS.between(joiningDate, now);

                        return (managerId == null || managerId.equalsIgnoreCase(currentManagerId)) &&
                            (yearsOfExperience == null || yearsOfExperienceCalculated >= yearsOfExperience);
                    }).map(emp -> new EmployeeResponseDTO(
                        emp.getId(),
                        emp.getName(),
                        emp.getDesignation(),
                        emp.getDepartment(),
                        emp.getEmail(),
                        emp.getMobile(),
                        emp.getLocation(),
                        emp.getDateOfJoining(),
                        emp.getCreatedTime(),
                        emp.getUpdatedTime()
                    ))
                    .collect(Collectors.toList());

                // Include the manager in the response if conditions are met
                if ((managerId == null || managerId.equalsIgnoreCase(currentManagerId)) && Integer.parseInt(currentManagerId) > 0 &&
                    (yearsOfExperience == null || employeeList.stream()
                        .anyMatch(employee -> {
                            LocalDateTime joiningDate = LocalDateTime.parse(employee.getDateOfJoining(), formatter);
                            LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
                            int yearsOfExperienceCalculated = (int) ChronoUnit.YEARS.between(joiningDate, now);
                            return yearsOfExperienceCalculated >= yearsOfExperience;
                        }))) {
                    return new EmployeeResponse(
                        managerName,
                        managerDept,
                        currentManagerId,
                        filteredEmployeeList
                    );
                } else {
                    return null;
                }
            })
            .filter(Objects::nonNull) // Remove null responses
            .collect(Collectors.toList());

        System.out.println(filteredResponses);

        String responseMessage = filteredResponses.isEmpty() ? "No employees found" : "Successfully fetched";

        EmployeeResponseGet response = new EmployeeResponseGet(responseMessage, filteredResponses);
        return ResponseEntity.ok(response);
            
        }
    
    public ResponseEntity<EmployeeResponseUpdate> deleteEmployee(String id) {
        if(employeeRepo.existsById(id)){
            Optional<EmployeeDetails> employeeDetails=employeeRepo.findById(id);

            if(employeeDetails.isPresent()){
                EmployeeDetails employeeResult=employeeDetails.get();

                if(employeeResult.getDesignation().matches("Account Manager")){
                    if(employeeRepo.findAllByManagerId(id).isEmpty()){
                        employeeRepo.deleteById(id);
                        EmployeeResponseUpdate employeeResponseUpdate=new EmployeeResponseUpdate("Successfully deleted " +employeeResult.getName()+ " from the employee list of the organization");
                        return ResponseEntity.ok(employeeResponseUpdate);
                    }
                    else{
                        EmployeeResponseUpdate employeeResponseUpdate=new EmployeeResponseUpdate("Cannot delete " +employeeDetails.get().getName()+ " because he manages other employees");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(employeeResponseUpdate);
                    }
                }
                else{
                    employeeRepo.deleteById(id);
                    EmployeeResponseUpdate employeeResponseUpdate=new EmployeeResponseUpdate("Successfully deleted " +employeeResult.getName()+ " from the employee list of the organization");
                    return ResponseEntity.ok(employeeResponseUpdate);
                }
            }
            else{
                EmployeeResponseUpdate employeeResponseUpdate=new EmployeeResponseUpdate("Employee not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(employeeResponseUpdate);
            }
        }

        else{
            EmployeeResponseUpdate employeeResponseUpdate=new EmployeeResponseUpdate("Employee not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(employeeResponseUpdate);
        }
    }
    public ResponseEntity<EmployeeResponseUpdate> updateEmployee(String id, String managerId) {

        Iterable<EmployeeDetails> listEmployeeDetails=employeeRepo.findAll();

        for(EmployeeDetails employeeDetails:listEmployeeDetails){
            if(employeeDetails.getManagerId().equals(id)){
                EmployeeResponseUpdate response = new EmployeeResponseUpdate("Manager id cannot be provided as employee id");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        }
        
        Optional<EmployeeDetails> employeeDetails=employeeRepo.findById(id);
        if(!employeeDetails.isPresent()){
            EmployeeResponseUpdate response = new EmployeeResponseUpdate("Employee not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        Optional<EmployeeDetails> oldManagerDetails=employeeRepo.findById(employeeDetails.get().getManagerId());
        Optional<EmployeeDetails> newManagerDetails=employeeRepo.findById(managerId);

        if (!newManagerDetails.isPresent()) {
            EmployeeResponseUpdate response = new EmployeeResponseUpdate("Manager not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        employeeDetails.get().setManagerId(managerId);
        Date today=new Date();
        employeeDetails.get().setUpdatedTime(today); 
        employeeRepo.save(employeeDetails.get());
        
        EmployeeResponseUpdate employeeResponseUpdate=new EmployeeResponseUpdate(employeeDetails.get().getName()+ "'s manager has successfully changed from " +oldManagerDetails.get().getName()+ " to "
        +newManagerDetails.get().getName());

        return ResponseEntity.ok(employeeResponseUpdate);
    }
    
}
