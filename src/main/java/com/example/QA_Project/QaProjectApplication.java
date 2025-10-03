package com.example.QA_Project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class QaProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(QaProjectApplication.class, args);
	}

}
