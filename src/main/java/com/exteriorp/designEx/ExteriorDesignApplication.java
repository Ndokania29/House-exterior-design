package com.exteriorp.designEx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class ExteriorDesignApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExteriorDesignApplication.class, args);
	}

}
