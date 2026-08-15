package com.mbaigo.datecentre.swingApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class SwingAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SwingAppApplication.class, args);
	}

}
