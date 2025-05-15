package com.example.FinalProject;

import com.example.FinalProject.FinalProjectApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import com.example.FinalProject.config.TestConfig;

@SpringBootTest(classes = FinalProjectApplication.class)
@Import(TestConfig.class)
class FinalProjectApplicationTests {

	@Test
	void contextLoads() {
	}

}
