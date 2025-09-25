package com.ifba.iotManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IotManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(IotManagementApplication.class, args);
	}
}
