package com.condominium;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CondominiumApplication {

	public static void main(String[] args) {
		SpringApplication.run(CondominiumApplication.class, args);
	}

}
