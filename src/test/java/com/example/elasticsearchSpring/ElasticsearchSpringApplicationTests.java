package com.example.elasticsearchSpring;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ElasticsearchSpringApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
    public void testMainMethod() {
        String[] args = {};
        
        ElasticsearchSpringApplication.main(args);
        
    }
}
