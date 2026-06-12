package com.adp.ExpenseService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class ExpenseServiceApplication {

	public static void main(String[] args) {

		SpringApplication.run(ExpenseServiceApplication.class, args);
		System.out.println("Hello Expense Service!! ");
	}

}
