package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.example.subapp.SideApplicationBootstrap;

@SpringBootApplication
public class ManualSpringAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(ManualSpringAppApplication.class, args);
	}
	
	@Bean
	SideApplicationBootstrap sideApplication(ApplicationContext hostAppCtx) {
		return new SideApplicationBootstrap(hostAppCtx);
	}
	
}
