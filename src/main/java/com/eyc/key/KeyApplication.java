package com.eyc.key;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class KeyApplication {

	public static void main(String[] args) {
		SpringApplication.run(KeyApplication.class, args);
	}

}
