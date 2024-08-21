package com.example.elasticsearchSpring.model;

import com.example.elasticsearchSpring.repository.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class IdGenerator {

    private static final AtomicInteger counter = new AtomicInteger(0);

    @Autowired
    public IdGenerator(EmployeeRepo employeeRepo) {
        initializeCounter(employeeRepo);
    }

    private static void initializeCounter(EmployeeRepo employeeRepo) {
        Optional<String> maxIdOpt = employeeRepo.findAll().stream()
                .map(EmployeeDetails::getId)
                .map(Integer::parseInt)
                .max(Integer::compareTo)
                .map(String::valueOf);

        int maxId = maxIdOpt.map(Integer::parseInt).orElse(0);
        counter.set(maxId);
    }

    public static String generateId() {
        return String.valueOf(counter.incrementAndGet());
    }
}
