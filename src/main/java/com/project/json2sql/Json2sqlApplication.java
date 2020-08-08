package com.project.json2sql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Json2sqlApplication {

	public static void main(String[] args) {
		SpringApplication.run(Json2sqlApplication.class, args);
	}

}
