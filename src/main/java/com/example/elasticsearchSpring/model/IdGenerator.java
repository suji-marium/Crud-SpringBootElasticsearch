package com.example.elasticsearchSpring.model;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {
    private static final AtomicInteger counter = new AtomicInteger(1);

    public static String generateId() {
        return String.valueOf(counter.getAndIncrement()); // Convert integer to string
    }
}
